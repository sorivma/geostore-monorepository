package sorivma.geogeometryservice.application.dto.response

import sorivma.geogeometryservice.domain.model.GeometryAuditLogAction
import java.time.OffsetDateTime
import java.util.*

data class GeometryAuditLogDto(
    val version: Int,
    val action: GeometryAuditLogAction,
    val userId: UUID,
    val timestamp: OffsetDateTime,
    val metadata: Map<String, Any> = emptyMap()
)