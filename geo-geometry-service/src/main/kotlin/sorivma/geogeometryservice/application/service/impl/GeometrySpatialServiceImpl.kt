package sorivma.geogeometryservice.application.service.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import sorivma.geogeometryservice.api.spatial.dto.BoundingBox
import sorivma.geogeometryservice.api.spatial.dto.response.BoundingBoxResponse
import sorivma.geogeometryservice.api.spatial.dto.response.CrsResponse
import sorivma.geogeometryservice.api.spatial.dto.response.FilterByBoundingBoxResponse
import sorivma.geogeometryservice.application.dto.response.GeometryDto
import sorivma.geogeometryservice.application.repository.SpatialQueryRepository
import sorivma.geogeometryservice.application.repository.SpatialRefSystemRepository
import sorivma.geogeometryservice.application.service.api.GeometryService
import sorivma.geogeometryservice.application.service.api.SpatialQueryService
import sorivma.geogeometryservice.config.geo.GeoProperties
import sorivma.geogeometryservice.domain.exception.SpatialRefSystemIsNotSupported
import sorivma.geogeometryservice.infrastructure.db.formatter.registry.GeometryFormatRegistry
import java.util.*

@Service
class GeometrySpatialServiceImpl(
    private val geometryService: GeometryService,
    private val spatialQueryRepository: SpatialQueryRepository,
    private val srsRepository: SpatialRefSystemRepository,
    private val formatRegistry: GeometryFormatRegistry,
    private val geoProperties: GeoProperties
) : SpatialQueryService {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun getBoundingBoxes(objectIds: List<UUID>, targetSrid: Int?): List<BoundingBoxResponse> {
        if (objectIds.isEmpty()) {
            log.warn("getBoundingBoxes: received empty objectIds → returning empty list")
            return emptyList()
        }

        val srid = resolveAndValidateSrid(targetSrid, "getBoundingBoxes")

        log.debug("getBoundingBoxes: resolved srid={}, objectIds={}", srid, objectIds)
        val result = spatialQueryRepository.findBoundingBoxes(objectIds, srid)
        log.info("getBoundingBoxes: found ${result.size} bounding boxes for ${objectIds.size} objects (srid=$srid)")
        return result
    }

    override fun filterByBoundingBox(
        objectIds: List<UUID>,
        bbox: BoundingBox,
        bboxSrid: Int?
    ): FilterByBoundingBoxResponse {
        if (objectIds.isEmpty()) {
            log.warn("filterByBoundingBox: received empty objectIds → returning empty list")
            return FilterByBoundingBoxResponse(emptyList())
        }

        val srid = resolveAndValidateSrid(bboxSrid, "filterByBoundingBox")

        log.debug("filterByBoundingBox: resolved bboxSrid={}, bbox={}, objectIds={}", srid, bbox, objectIds)
        val matched = spatialQueryRepository.findIntersectingObjectIds(objectIds, bbox, srid, geoProperties.defaultSrid)
        log.info("filterByBoundingBox: matched ${matched.size} objectIds out of ${objectIds.size} (bboxSrid=$srid)")
        return FilterByBoundingBoxResponse(matched)
    }

    override fun filterAndFetch(objectIds: List<UUID>, bbox: BoundingBox, format: String, srid: Int?): List<GeometryDto> {
        val bboxSrid = resolveAndValidateSrid(srid, "filterAndFetch")

        log.info("filterAndFetch: filtering ${objectIds.size} objects by bbox $bbox in srid=$bboxSrid")

        val matched = spatialQueryRepository.findIntersectingObjectIds(
            objectIds = objectIds,
            bbox = bbox,
            bboxSrid = bboxSrid,
            targetSrid = geoProperties.defaultSrid,
        )

        log.info("filterAndFetch: ${matched.size} objects matched bbox")

        return matched.mapNotNull { objectId ->
            try {
                geometryService.getCurrent(UUID.fromString(objectId), format, bboxSrid)
            } catch (ex: Exception) {
                log.warn("failed to fetch geometry for $objectId - ${ex.message}")
                null
            }
        }
    }

    override fun getCrs(objectIds: List<UUID>): List<CrsResponse> {
        if (objectIds.isEmpty()) {
            log.warn("getCrs: received empty objectIds → returning empty list")
            return emptyList()
        }

        log.debug("getCrs: resolving SRIDs for objectIds={}", objectIds)
        val entries = spatialQueryRepository.findCrsByObjectIds(objectIds)
        log.info("getCrs: resolved ${entries.size} entries from ${objectIds.size} input IDs")
        return entries
    }

    private fun resolveAndValidateSrid(srid: Int?, context: String): Int {
        val finalSrid = srid ?: geoProperties.defaultSrid
        if (!srsRepository.isSupported(finalSrid)) {
            log.error("$context: SRID $finalSrid is not supported")
            throw SpatialRefSystemIsNotSupported(finalSrid)
        }
        log.debug("$context: SRID $finalSrid validated successfully")
        return finalSrid
    }
}