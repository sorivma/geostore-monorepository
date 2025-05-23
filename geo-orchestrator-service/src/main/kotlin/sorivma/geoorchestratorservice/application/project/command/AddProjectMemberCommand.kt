package sorivma.geoorchestratorservice.application.project.command

import sorivma.geoorchestratorservice.domain.model.project.ProjectRole
import java.util.*

data class AddProjectMemberCommand(
    val projectId: UUID,
    val userId: UUID,
    val role: ProjectRole
)