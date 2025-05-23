package sorivma.geoorchestratorservice.infrastructure.query.dto

data class PageRequest(
    val page: Int = 0,
    val size: Int = 20
)
