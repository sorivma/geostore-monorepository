package sorivma.geoorchestratorservice.domain.model.layer

import java.util.*

data class VectorLayerData(
    override val layerId: UUID,
    val objectIds: List<UUID>,
    val style: VectorStyle = VectorStyle(),
): LayerData {
    data class VectorStyle(
        val fillColor: String = "#3388ff",
        val strokeColor: String = "#000000",
        val strokeWidth: Double = 0.0,
    )
}
