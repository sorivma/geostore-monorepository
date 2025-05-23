package sorivma.geosearchindexer.application.service.query

import org.opensearch.client.opensearch._types.SortOptions
import org.opensearch.client.opensearch._types.query_dsl.Query

interface QueryBuilder<R> {
    fun buildQuery(request: R): Query
    fun buildSort(request: R): List<SortOptions>
}