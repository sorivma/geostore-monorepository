package sorivma.geosearchindexer.application.service.dto

data class SearchResultPage<T>(
    val total: Long,
    val items: List<T>,
)
