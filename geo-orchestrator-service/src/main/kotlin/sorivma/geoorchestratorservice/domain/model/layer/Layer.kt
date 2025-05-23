package sorivma.geoorchestratorservice.domain.model.layer

import java.util.*

data class Layer(
    val id: UUID,
    val projectId: UUID,
    val name: String,
    val geometryType: GeometryType,
    val type: LayerType,
    val order: Int
) {
    enum class GeometryType {
        POINT,
        LINESTRING,
        POLYGON,
        RASTER
    }
}
