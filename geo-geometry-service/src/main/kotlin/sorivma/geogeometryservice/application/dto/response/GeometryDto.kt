package sorivma.geogeometryservice.application.dto.response

import java.time.OffsetDateTime
import java.util.*

data class GeometryDto(
    val objectId: UUID,
    val version: Int,
    val timestamp: OffsetDateTime,
    val geometry: Any,
    val format: String
)