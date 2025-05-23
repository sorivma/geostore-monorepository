package sorivma.geoorchestratorservice.infrastructure.persistence.layer.repository

import sorivma.geoorchestratorservice.domain.model.layer.RasterLayerData
import java.util.*

interface RasterLayerDataRepository {
    fun save(data: RasterLayerData)
    fun findByLayerId(layerId: UUID): RasterLayerData?
    fun delete(layerId: UUID)
}