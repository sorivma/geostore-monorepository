package sorivma.geoorchestratorservice.application.layer.dto

import sorivma.geoorchestratorservice.domain.model.layer.Layer
import sorivma.geoorchestratorservice.domain.model.layer.LayerType
import java.util.UUID

data class CreateLayerDto(
    val projectId: UUID,
    val name: String,
    val type: LayerType,
    val geometryType: Layer.GeometryType,
    val order: Int
)