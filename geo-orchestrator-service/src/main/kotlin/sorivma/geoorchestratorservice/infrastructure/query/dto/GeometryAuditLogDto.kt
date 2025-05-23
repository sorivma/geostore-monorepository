package sorivma.geoorchestratorservice.infrastructure.query.dto

import java.time.OffsetDateTime
import java.util.*

data class GeometryAuditLogDto(
    val version: Int,
    val action: GeometryAuditLogAction,
    val userId: UUID,
    val timestamp: OffsetDateTime,
    val metadata: Map<String, Any> = emptyMap()
) {
    enum class GeometryAuditLogAction {
        CREATE,
        UPDATE,
        DELETE,
        ROLLBACK
    }
}