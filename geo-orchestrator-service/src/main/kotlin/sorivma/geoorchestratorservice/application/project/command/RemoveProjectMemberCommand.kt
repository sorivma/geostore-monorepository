package sorivma.geoorchestratorservice.application.project.command

import java.util.*

data class RemoveProjectMemberCommand(
    val projectId: UUID,
    val userId: UUID
)