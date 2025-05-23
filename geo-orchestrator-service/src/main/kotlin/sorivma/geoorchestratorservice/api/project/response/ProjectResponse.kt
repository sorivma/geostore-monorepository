package sorivma.geoorchestratorservice.api.project.response

import sorivma.geoorchestratorservice.domain.model.project.Project
import java.util.*

data class ProjectResponse(
    val id: UUID,
    val name: String,
    val description: String?,
    val ownerId: UUID
) {
    companion object {
        fun fromDomain(project: Project) = ProjectResponse(
            id = project.id,
            name = project.name,
            description = project.description,
            ownerId = project.ownerId
        )
    }
}