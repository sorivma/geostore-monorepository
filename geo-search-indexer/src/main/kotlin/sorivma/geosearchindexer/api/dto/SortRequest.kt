package sorivma.geosearchindexer.api.dto

data class SortRequest(
    val field: String = "indexedAt",
    val order: SortOrder = SortOrder.Desc,
) {
    enum class SortOrder { Asc, Desc }
}
