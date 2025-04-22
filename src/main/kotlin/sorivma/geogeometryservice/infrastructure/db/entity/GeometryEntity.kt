package sorivma.geogeometryservice.infrastructure.db.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.OffsetDateTime
import java.util.*

@Table("geometries")
class GeometryEntity(
    @Id
    val id: UUID,
    val objectId: UUID,
    val version: Int,
    val timestamp: OffsetDateTime,
    val active: Boolean,
    val deleted: Boolean,
)