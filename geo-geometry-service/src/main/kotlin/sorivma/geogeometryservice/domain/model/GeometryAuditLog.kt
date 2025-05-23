package sorivma.geogeometryservice.domain.model

import java.time.OffsetDateTime
import java.util.*

data class GeometryAuditLog(
    val id: UUID,
    val objectId: UUID,
    val version: Int,
    val action: GeometryAuditLogAction,
    val userId: UUID,
    val timestamp: OffsetDateTime,
    val metadata: Map<String, Any> = emptyMap()
)