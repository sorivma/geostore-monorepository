package sorivma.geoorchestratorservice.domain.model.project

import java.util.*

data class ProjectMember(
    val userId: UUID,
    val role: ProjectRole
)