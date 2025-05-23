package sorivma.geoorchestratorservice.application.project

import sorivma.geoorchestratorservice.application.project.command.AddProjectMemberCommand
import sorivma.geoorchestratorservice.application.project.command.CreateProjectCommand
import sorivma.geoorchestratorservice.application.project.command.RemoveProjectMemberCommand
import sorivma.geoorchestratorservice.application.project.command.UpdateProjectMemberRoleCommand
import sorivma.geoorchestratorservice.domain.model.project.Project
import java.util.*

interface ProjectService {
    fun create(command: CreateProjectCommand): UUID
    fun getById(projectId: UUID): Project
    fun delete(projectId: UUID)
    fun addMember(command: AddProjectMemberCommand)
    fun removeMember(command: RemoveProjectMemberCommand)
    fun updateMemberRole(command: UpdateProjectMemberRoleCommand)
    fun getOwnedBy(): List<Project>
    fun getVisibleTo(): List<Project>
}