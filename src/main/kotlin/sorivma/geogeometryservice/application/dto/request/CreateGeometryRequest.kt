package sorivma.geogeometryservice.application.dto.request

import java.util.*

data class CreateGeometryRequest(
    val objectId: UUID,
    val geometry: String,
    val format: String,
    val sourceSrid: Int
)
