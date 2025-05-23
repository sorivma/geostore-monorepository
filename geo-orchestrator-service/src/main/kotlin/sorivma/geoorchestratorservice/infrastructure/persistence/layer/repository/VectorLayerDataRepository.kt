package sorivma.geoorchestratorservice.infrastructure.persistence.layer.repository

import sorivma.geoorchestratorservice.domain.model.layer.VectorLayerData
import java.util.*

interface VectorLayerDataRepository {
    fun save(data: VectorLayerData)
    fun findByLayerId(layerId: UUID): VectorLayerData?
    fun deleteByLayerId(layerId: UUID)
    fun deleteObject(layerId: UUID, objectId: UUID)
}