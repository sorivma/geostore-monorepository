package sorivma.geoorchestratorservice.infrastructure.persistence.layer.repository

import sorivma.geoorchestratorservice.domain.model.layer.Layer
import java.util.*

interface LayerRepository {
    fun save(layer: Layer): Layer
    fun findById(id: UUID): Layer?
    fun delete(id: UUID)
    fun findByProject(projectId: UUID): List<Layer>
}