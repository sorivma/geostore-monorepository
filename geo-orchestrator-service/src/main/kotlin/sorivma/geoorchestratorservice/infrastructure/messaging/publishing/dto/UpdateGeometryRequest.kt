package sorivma.geoorchestratorservice.infrastructure.messaging.publishing.dto

import java.util.*

data class UpdateGeometryRequest(
    val objectId: UUID,
    val newGeometry: String,
    val format: String,
    val sourceSrid: Int
)
