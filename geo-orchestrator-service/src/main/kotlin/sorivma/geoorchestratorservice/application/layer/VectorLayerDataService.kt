package sorivma.geoorchestratorservice.application.layer

import sorivma.geoorchestratorservice.application.layer.dto.VectorLayerDataDto
import java.util.*

interface VectorLayerDataService {
    fun save(layerId: UUID, data: VectorLayerDataDto)
    fun getByLayerId(layerId: UUID): VectorLayerDataDto
    fun deleteByLayerId(layerId: UUID)
    fun delete(objectId: UUID, layerId: UUID)
    fun existsByLayerId(layerId: UUID): Boolean
}