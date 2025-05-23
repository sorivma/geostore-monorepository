package sorivma.geoorchestratorservice.application.project.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sorivma.geoorchestratorservice.application.layer.LayerService
import sorivma.geoorchestratorservice.application.project.ProjectService
import sorivma.geoorchestratorservice.application.project.command.AddProjectMemberCommand
import sorivma.geoorchestratorservice.application.project.command.CreateProjectCommand
import sorivma.geoorchestratorservice.application.project.command.RemoveProjectMemberCommand
import sorivma.geoorchestratorservice.application.project.command.UpdateProjectMemberRoleCommand
import sorivma.geoorchestratorservice.domain.model.project.Project
import sorivma.geoorchestratorservice.infrastructure.persistence.project.repository.ProjectRepository
import sorivma.geoorchestratorservice.shared.exception.ProjectAccessDeniedException
import sorivma.geoorchestratorservice.shared.exception.ProjectNotFoundException
import sorivma.geoorchestratorservice.shared.security.SecurityContextHolderFacade
import java.util.*

@Service
class DefaultProjectService(
    private val repository: ProjectRepository,
    private val layerService: LayerService,
    private val security: SecurityContextHolderFacade
): ProjectService {
    private val log = LoggerFactory.getLogger(DefaultProjectService::class.java)

    override fun create(command: CreateProjectCommand): UUID {
        val currentUser = security.currentUserId()

        val project = Project(
            id = UUID.randomUUID(),
            name = command.name,
            description = command.description,
            ownerId = currentUser
        )
        val saved = repository.save(project)

        log.info("Created project projectId=${saved.id} ownerId=$currentUser")
        return saved.id
    }

    override fun getById(projectId: UUID): Project {
        val currentUser = security.currentUserId()

        val project = repository.findById(projectId)
            ?: throw ProjectNotFoundException(projectId)

        project.assertViewAccess(currentUser)

        log.info("Accessed project projectId=$projectId userId=$currentUser")
        return project
    }

    @Transactional
    override fun delete(projectId: UUID) {
        val currentUser = security.currentUserId()

        val project = repository.findById(projectId)
            ?: throw ProjectNotFoundException(projectId)

        if (project.ownerId != currentUser) throw ProjectAccessDeniedException()

        val layers = layerService.getByProjectId(projectId)
        layers.forEach { layer ->
            layerService.deleteById(layer.id)
        }

        repository.deleteById(projectId)

        log.info("Deleted project projectId=$projectId deletedBy=$currentUser")
    }

    override fun addMember(command: AddProjectMemberCommand) {
        val currentUser = security.currentUserId()

        val project = repository.findById(command.projectId)
            ?: throw ProjectNotFoundException(command.projectId)

        if (project.ownerId != currentUser) throw ProjectAccessDeniedException()

        project.addMember(command.userId, command.role)
        repository.save(project)

        log.info("Added member to project projectId=${command.projectId} memberId=${command.userId} role=${command.role} addedBy=$currentUser")
    }

    override fun removeMember(command: RemoveProjectMemberCommand) {
        val currentUser = security.currentUserId()

        val project = repository.findById(command.projectId)
            ?: throw ProjectNotFoundException(command.projectId)

        if (project.ownerId != currentUser) throw ProjectAccessDeniedException()

        project.removeMember(command.userId)
        repository.save(project)

        log.info("Removed member from project projectId=${command.projectId} memberId=${command.userId} removedBy=$currentUser")
    }

    override fun updateMemberRole(command: UpdateProjectMemberRoleCommand) {
        val currentUser = security.currentUserId()

        val project = repository.findById(command.projectId)
            ?: throw ProjectNotFoundException(command.projectId)

        if (project.ownerId != currentUser) throw ProjectAccessDeniedException()

        project.updateMemberRole(command.userId, command.newRole)
        repository.save(project)

        log.info("Updated member role in project projectId=${command.projectId} memberId=${command.userId} newRole=${command.newRole} updatedBy=$currentUser")
    }

    override fun getOwnedBy(): List<Project> {
        val currentUser = security.currentUserId()
        val list = repository.findByOwnerId(currentUser)
        log.info("Fetched owned projects userId={} count={}", currentUser, list.size)
        return list
    }

    override fun getVisibleTo(): List<Project> {
        val currentUser = security.currentUserId()
        val list = repository.findAllVisibleTo(currentUser)
        log.info("Fetched visible projects userId=$currentUser count=${list.size}")
        return list
    }

}