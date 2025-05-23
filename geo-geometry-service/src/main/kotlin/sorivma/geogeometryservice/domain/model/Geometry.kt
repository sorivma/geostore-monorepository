package sorivma.geogeometryservice.domain.model

import java.time.OffsetDateTime
import java.util.*

data class Geometry(
    val id: UUID,
    val objectId: UUID,
    val version: Int,
    val timestamp: OffsetDateTime,
    val active: Boolean,
    val deleted: Boolean,
)