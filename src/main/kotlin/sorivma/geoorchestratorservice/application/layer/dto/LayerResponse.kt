package sorivma.geoorchestratorservice.application.layer.dto

import sorivma.geoorchestratorservice.domain.model.layer.LayerType
import java.util.*

data class LayerResponse(
    val id: UUID,
    val projectId: UUID,
    val name: String,
    val type: LayerType,
    val order: Int
)