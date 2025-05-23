package sorivma.geoorchestratorservice.application.project.command

import java.util.*

data class CreateProjectCommand(
    val name: String,
    val description: String?,
)