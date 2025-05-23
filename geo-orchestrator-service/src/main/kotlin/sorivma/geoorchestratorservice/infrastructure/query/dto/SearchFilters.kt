package sorivma.geoorchestratorservice.infrastructure.query.dto

data class SearchFilters(
    val dcType: String? = null,
    val region: String? = null,
    val topicCategory: List<String>? = null
)
