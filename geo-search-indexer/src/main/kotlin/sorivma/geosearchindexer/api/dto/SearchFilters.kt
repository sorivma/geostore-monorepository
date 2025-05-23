package sorivma.geosearchindexer.api.dto

data class SearchFilters(
    val dcType: String? = null,
    val region: String? = null,
    val topicCategory: List<String>? = null
)
