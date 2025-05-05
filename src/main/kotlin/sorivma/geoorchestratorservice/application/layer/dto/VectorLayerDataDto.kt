package sorivma.geoorchestratorservice.application.layer.dto

import java.util.*

data class VectorLayerDataDto(
    val objectIds: List<UUID>,
    val fillColor: String,
    val strokeColor: String,
    val strokeWidth: Double
)
