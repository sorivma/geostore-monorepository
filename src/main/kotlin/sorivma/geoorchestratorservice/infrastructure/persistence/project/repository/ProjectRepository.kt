package sorivma.geoorchestratorservice.infrastructure.persistence.project.repository

import sorivma.geoorchestratorservice.domain.model.project.Project
import java.util.*

interface ProjectRepository {
    fun save(project: Project): Project
    fun findById(id: UUID): Project?
    fun deleteById(id: UUID)
    fun existsById(id: UUID): Boolean
    fun findByOwnerId(ownerId: UUID): List<Project>
    fun findAllVisibleTo(userId: UUID): List<Project>
}