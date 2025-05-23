package sorivma.geogeometryservice.application.service.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sorivma.geogeometryservice.application.dto.request.CreateGeometryRequest
import sorivma.geogeometryservice.application.dto.request.RollbackGeometryRequest
import sorivma.geogeometryservice.application.dto.request.UpdateGeometryRequest
import sorivma.geogeometryservice.application.dto.response.GeometryAuditLogDto
import sorivma.geogeometryservice.application.dto.response.GeometryDto
import sorivma.geogeometryservice.application.repository.FormattedGeometryRepository
import sorivma.geogeometryservice.application.repository.SpatialRefSystemRepository
import sorivma.geogeometryservice.application.service.api.GeometryAuditLogService
import sorivma.geogeometryservice.application.service.api.GeometryService
import sorivma.geogeometryservice.config.geo.GeoProperties
import sorivma.geogeometryservice.domain.exception.DuplicateObjectIdException
import sorivma.geogeometryservice.domain.exception.GeometryNotFoundException
import sorivma.geogeometryservice.domain.exception.SpatialRefSystemIsNotSupported
import sorivma.geogeometryservice.domain.model.Geometry
import sorivma.geogeometryservice.domain.model.GeometryAuditLogAction
import sorivma.geogeometryservice.domain.repository.GeometryRepository
import java.time.Clock
import java.time.OffsetDateTime
import java.util.*

@Service
class GeometryServiceImpl(
    private val geometryRepository: GeometryRepository,
    private val spatialRefSystemRepository: SpatialRefSystemRepository,
    private val formattedGeometryRepository: FormattedGeometryRepository,
    private val geoProperties: GeoProperties,
    private val auditLogService: GeometryAuditLogService,
) : GeometryService {
    private val log = LoggerFactory.getLogger(GeometryServiceImpl::class.java)
    private val clock: Clock = Clock.systemUTC()

    @Transactional
    override fun create(request: CreateGeometryRequest): GeometryDto {
        log.info("Creating geometry for objectId=${request.objectId}, format=${request.format}")

        geometryRepository.findActiveByObjectId(request.objectId)?.let {
            throw DuplicateObjectIdException(request.objectId)
        }

        val now = OffsetDateTime.now(clock)

        val geometry = Geometry(
            id = UUID.randomUUID(),
            objectId = request.objectId,
            version = 1,
            timestamp = now,
            active = true,
            deleted = false
        )

        geometryRepository.save(geometry, request.geometry, request.format, request.sourceSrid)
        auditLogService.record(geometry, GeometryAuditLogAction.CREATE, now)

        return toDto(geometry, request.format, request.sourceSrid)
    }

    @Transactional
    override fun update(request: UpdateGeometryRequest): GeometryDto {
        log.info("Updating geometry for objectId=${request.objectId}, format=${request.format}")
        val current = geometryRepository.findActiveByObjectId(request.objectId)
            ?: throw GeometryNotFoundException(request.objectId)

        val now = OffsetDateTime.now(clock)
        val updated = current.copy(
            id = UUID.randomUUID(),
            version = current.version + 1,
            timestamp = now,
            active = true,
            deleted = false
        )

        geometryRepository.deactivateCurrentVersion(request.objectId)
        geometryRepository.save(updated, request.newGeometry, request.format, request.sourceSrid)
        auditLogService.record(updated, GeometryAuditLogAction.UPDATE, now)

        return toDto(updated, request.format, request.sourceSrid)
    }

    @Transactional
    override fun rollback(request: RollbackGeometryRequest): GeometryDto {
        log.info("Rolling back geometry for objectId=${request.objectId} to version=${request.targetVersion}")
        val target = geometryRepository.findVersionByObjectId(request.objectId, request.targetVersion)
            ?: throw GeometryNotFoundException(request.objectId)

        val now = OffsetDateTime.now(clock)
        val rollbacked = target.copy(
            id = UUID.randomUUID(),
            version = target.version + 1,
            timestamp = now,
            active = true,
            deleted = false
        )

        geometryRepository.deactivateCurrentVersion(request.objectId)
        geometryRepository.save(rollbacked, getRawGeometry(request.objectId, request.targetVersion), "wkb", geoProperties.defaultSrid)
        auditLogService.record(rollbacked, GeometryAuditLogAction.ROLLBACK, now)

        return toDto(rollbacked, "wkb", geoProperties.defaultSrid)
    }

    @Transactional
    override fun delete(objectId: UUID) {
        log.info("Deleting geometry for objectId=$objectId")
        val current = geometryRepository.findActiveByObjectId(objectId)
            ?: throw GeometryNotFoundException(objectId)

        val now = OffsetDateTime.now(clock)
        val deleted = current.copy(
            id = UUID.randomUUID(),
            version = current.version + 1,
            timestamp = now,
            active = false,
            deleted = true
        )

        geometryRepository.deactivateCurrentVersion(objectId)
        geometryRepository.save(deleted, getRawGeometry(objectId, current.version), "geojson", geoProperties.defaultSrid)
        auditLogService.record(deleted, GeometryAuditLogAction.DELETE, now)
    }

    override fun getCurrent(objectId: UUID, format: String, targetSrid: Int?): GeometryDto {
        val current = geometryRepository.findActiveByObjectId(objectId)
            ?: throw GeometryNotFoundException(objectId)

        return toDto(current, format, targetSrid ?: geoProperties.defaultSrid)
    }

    override fun getAllVersions(objectId: UUID, format: String, targetSrid: Int?): List<GeometryDto> {
        return geometryRepository.findAllVersionsByObjectId(objectId)
            .map { toDto(it, format, targetSrid ?: geoProperties.defaultSrid) }
    }

    override fun getAuditLogs(objectId: UUID): List<GeometryAuditLogDto> {
        return auditLogService.getLogs(objectId).map {
            GeometryAuditLogDto(
                action = it.action,
                userId = it.userId,
                timestamp = it.timestamp,
                version = it.version
            )
        }
    }

    private fun toDto(geometry: Geometry, format: String, srid: Int?): GeometryDto {
        srid?.let {
            if (!spatialRefSystemRepository.isSupported(it)) {
                throw SpatialRefSystemIsNotSupported(srid)
            }
        }

        val formatted = formattedGeometryRepository.findFormatted(geometry.objectId, geometry.version, format, srid) ?: throw GeometryNotFoundException(geometry.objectId)
        return GeometryDto(
            objectId = geometry.objectId,
            version = geometry.version,
            timestamp = geometry.timestamp,
            format = formatted.format,
            geometry = formatted.geometry
        )
    }

    private fun getRawGeometry(objectId: UUID, version: Int): String {
        val formatted = formattedGeometryRepository.findFormatted(objectId, version, "wkt") ?: throw GeometryNotFoundException(objectId)
        return formatted.geometry as? String
            ?: throw IllegalStateException("Expected raw geometry as String, got: ${formatted.geometry::class.simpleName}")
    }
}