package sorivma.geoorchestratorservice.api.layer.request

import sorivma.geoorchestratorservice.domain.model.layer.Layer
import sorivma.geoorchestratorservice.domain.model.layer.LayerType

data class CreateLayerRequest(
    val name: String,
    val type: LayerType,
    val geometryType: Layer.GeometryType,
    val order: Int
)
