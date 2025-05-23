package sorivma.geoorchestratorservice.application.vectorobject.impl

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import sorivma.geoorchestratorservice.infrastructure.query.dto.MetadataDto
import sorivma.geoorchestratorservice.application.project.ProjectAccessService
import sorivma.geoorchestratorservice.application.vectorobject.VectorObjectQueryService
import sorivma.geoorchestratorservice.application.vectorobject.dto.BboxDto
import sorivma.geoorchestratorservice.application.vectorobject.dto.VectorObjectDto
import sorivma.geoorchestratorservice.domain.model.layer.Layer
import sorivma.geoorchestratorservice.infrastructure.persistence.layer.repository.LayerRepository
import sorivma.geoorchestratorservice.infrastructure.persistence.layer.repository.VectorLayerDataRepository
import sorivma.geoorchestratorservice.infrastructure.query.GeometryQueryClient
import sorivma.geoorchestratorservice.infrastructure.query.MetadataQueryClient
import sorivma.geoorchestratorservice.infrastructure.query.dto.FilterByBoundingBoxesRequest
import sorivma.geoorchestratorservice.shared.exception.ObjectNotFoundInLayerException
import java.util.*

@Service
class DefaultVectorObjectQueryService(
    private val vectorRepo: VectorLayerDataRepository,
    private val geometryClient: GeometryQueryClient,
    private val metadataClient: MetadataQueryClient,
    private val layerRepository: LayerRepository,
    private val accessService: ProjectAccessService
) : VectorObjectQueryService {

    override fun getAll(
        layerId: UUID,
        includeMetadata: Boolean,
        format: String,
        srid: Int
    ): List<VectorObjectDto> {
        val layer = getLayerAndAssertView(layerId)
        val data = vectorRepo.findByLayerId(layer.id) ?: return emptyList()

        val geometries = geometryClient.getByObjectIds(data.objectIds, format, srid, bearerToken())
        val metadataMap = if (includeMetadata) {
            metadataClient.getByObjectIds(data.objectIds, bearerToken())
                .associateBy { it.objectId }
        } else emptyMap()

        return geometries.map { geom ->
            VectorObjectDto(
                objectId = geom.objectId,
                geometry = geom.geometry,
                metadata = metadataMap[geom.objectId.toString()]?.let { flattenMetadata(it) }
            )
        }
    }

    override fun getByBbox(
        layerId: UUID,
        bbox: BboxDto,
        srid: Int,
        includeMetadata: Boolean,
        format: String
    ): List<VectorObjectDto> {
        val layer = getLayerAndAssertView(layerId)
        val data = vectorRepo.findByLayerId(layer.id) ?: return emptyList()

        val filteredGeometries = geometryClient.filterAndFetchBbox(
            request = FilterByBoundingBoxesRequest(
                bbox = bbox,
                objectIds = data.objectIds.map { it.toString() },
            ),
            srid = srid,
            format = format,
            authHeader = bearerToken()
        )
        val metadataMap: Map<String, MetadataDto> = if (includeMetadata) {
            metadataClient.getByObjectIds(filteredGeometries.map { it.objectId }, bearerToken())
                .associateBy { it.objectId }
        } else emptyMap()

        return filteredGeometries.map { geom ->
            VectorObjectDto(
                objectId = geom.objectId,
                geometry = geom.geometry,
                metadata = metadataMap[geom.objectId.toString()]?.let { flattenMetadata(it) }
            )
        }
    }

    override fun getByObjectId(
        layerId: UUID,
        objectId: UUID,
        includeMetadata: Boolean,
        format: String,
        srid: Int
    ): VectorObjectDto {
        val layer = getLayerAndAssertView(layerId)

        val data = vectorRepo.findByLayerId(layer.id)
            ?: throw ObjectNotFoundInLayerException(objectId, layerId)

        if (objectId !in data.objectIds) {
            throw ObjectNotFoundInLayerException(objectId, layerId)
        }

        val geometry = geometryClient.getCurrent(
            objectId = objectId,
            format = format,
            srid = srid,
            authHeader = bearerToken()
        )

        val metadata = if (includeMetadata) {
            metadataClient.getByObjectId(
                objectId = objectId,
                authHeader = bearerToken()
            ).let { flattenMetadata(it) }
        } else null

        return VectorObjectDto(
            objectId = objectId,
            geometry = geometry.geometry,
            metadata = metadata
        )
    }

    private fun getLayerAndAssertView(layerId: UUID): Layer {
        val layer = layerRepository.findById(layerId)
            ?: throw IllegalArgumentException("Layer not found: $layerId")

        accessService.assertCanView(layer.projectId)
        return layer
    }

    private fun flattenMetadata(meta: MetadataDto): Map<String, Any?> = buildMap {
        meta.temporalExtent?.let {
            put("temporalExtent", mapOf("start" to it.start, "end" to it.end))
        }
        meta.source?.let { put("source", it) }
        meta.region?.let { put("region", it) }
        meta.topicCategory?.let { put("topicCategory", it) }
        meta.properties?.forEach { (k, v) -> put(k, v) }
    }

    private fun bearerToken(): String {
        val auth = SecurityContextHolder.getContext().authentication
        val token = (auth.credentials as? Jwt) ?: throw IllegalStateException("No token found")
        return "Bearer ${token.tokenValue}"
    }
}