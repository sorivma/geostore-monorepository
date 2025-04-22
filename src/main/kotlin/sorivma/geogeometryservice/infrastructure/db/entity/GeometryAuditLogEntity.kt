package sorivma.geogeometryservice.infrastructure.db.entity

import org.springframework.data.annotation.Id
import java.time.OffsetDateTime
import java.util.*

data class GeometryAuditLogEntity(
    @Id
    val id: UUID,
    val objectId: UUID,
    val version: Int,
    val action: String,
    val userId: UUID,
    val timestamp: OffsetDateTime,
    val metadata: String?
)