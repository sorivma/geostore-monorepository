package sorivma.geoorchestratorservice.application.layer

import sorivma.geoorchestratorservice.application.layer.dto.RasterLayerDataDto
import java.util.*

interface RasterLayerDataService {
    fun save(layerId: UUID, data: RasterLayerDataDto)
    fun getByLayerId(layerId: UUID): RasterLayerDataDto
    fun deleteByLayerId(layerId: UUID)
}