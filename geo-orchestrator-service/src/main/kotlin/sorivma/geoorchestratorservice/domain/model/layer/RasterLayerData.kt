package sorivma.geoorchestratorservice.domain.model.layer

import java.util.UUID

data class RasterLayerData(
    override val layerId: UUID,
    val cogUrl: String,
    val tileJsonUrl: String,
    val attribution: String? = null,
    val style: RasterStyle = RasterStyle(),
): LayerData {
    data class RasterStyle(
        val opacity: Double = 0.0,
    )
}