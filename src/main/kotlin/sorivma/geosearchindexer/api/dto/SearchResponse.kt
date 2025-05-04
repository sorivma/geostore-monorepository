package sorivma.geosearchindexer.api.dto

data class SearchResponse<T>(
    val total: Long,
    val page: Int,
    val size: Int,
    val items: List<T>
)