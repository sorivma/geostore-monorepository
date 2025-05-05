package sorivma.geoorchestratorservice.infrastructure.query.dto

import java.time.OffsetDateTime
import java.util.*

data class GeometryDto(
    val objectId: UUID,
    val geometry: Any,
    val format: String,
    val version: Int,
    val timestamp: OffsetDateTime
)