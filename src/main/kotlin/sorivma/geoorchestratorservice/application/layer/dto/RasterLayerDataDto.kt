package sorivma.geoorchestratorservice.application.layer.dto

data class RasterLayerDataDto(
    val cogUrl: String,
    val tileJsonUrl: String,
    val attribution: String?,
    val opacity: Double
)
