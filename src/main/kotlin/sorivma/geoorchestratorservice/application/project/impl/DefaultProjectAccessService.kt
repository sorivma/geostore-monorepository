package sorivma.geoorchestratorservice.application.project.impl

import org.springframework.stereotype.Service
import sorivma.geoorchestratorservice.application.project.ProjectAccessService
import sorivma.geoorchestratorservice.infrastructure.persistence.project.repository.ProjectRepository
import sorivma.geoorchestratorservice.shared.exception.ProjectAccessDeniedException
import sorivma.geoorchestratorservice.shared.exception.ProjectNotFoundException
import sorivma.geoorchestratorservice.shared.security.SecurityContextHolderFacade
import java.util.*

@Service
class DefaultProjectAccessService(
    private val projectRepository: ProjectRepository,
    private val security: SecurityContextHolderFacade
) : ProjectAccessService {

    override fun assertCanEdit(projectId: UUID) {
        val userId = security.currentUserId()
        val project = projectRepository.findById(projectId)
            ?: throw ProjectNotFoundException(projectId)

        if (!project.canEdit(userId)) {
            throw ProjectAccessDeniedException()
        }
    }

    override fun assertCanView(projectId: UUID) {
        val userId = security.currentUserId()
        val project = projectRepository.findById(projectId)
            ?: throw ProjectNotFoundException(projectId)

        if (!project.canView(userId)) {
            throw ProjectAccessDeniedException()
        }
    }
}