package sorivma.geogeometryservice.application.service.api

import sorivma.geogeometryservice.application.dto.response.GeometryAuditLogDto
import sorivma.geogeometryservice.domain.model.Geometry
import sorivma.geogeometryservice.domain.model.GeometryAuditLogAction
import java.time.OffsetDateTime
import java.util.*

interface GeometryAuditLogService {
    fun record(
        geometry: Geometry,
        action: GeometryAuditLogAction,
        timestamp: OffsetDateTime
    )

    fun getLogs(objectId: UUID): List<GeometryAuditLogDto>
}