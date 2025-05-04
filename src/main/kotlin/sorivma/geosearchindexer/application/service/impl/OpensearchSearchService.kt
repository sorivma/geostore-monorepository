package sorivma.geosearchindexer.application.service.impl

import org.opensearch.client.opensearch.OpenSearchClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import sorivma.geosearchindexer.api.dto.SearchQueryRequest
import sorivma.geosearchindexer.application.service.SearchService
import sorivma.geosearchindexer.application.service.dto.SearchResultPage
import sorivma.geosearchindexer.application.service.query.QueryBuilder
import sorivma.geosearchindexer.domain.model.IndexedMetadata
import sorivma.geosearchindexer.infrastructure.config.opensearch.properties.OpenSearchProperties

@Service
class OpensearchSearchService(
    private val client: OpenSearchClient,
    private val queryBuilder: QueryBuilder<SearchQueryRequest>,
    private val props: OpenSearchProperties,
) : SearchService {

    private val log = LoggerFactory.getLogger(OpensearchSearchService::class.java)

    override fun search(request: SearchQueryRequest): SearchResultPage<IndexedMetadata> {
        log.info("Search request: objectIds=${request.objectIds.size}, q='${request.query}', filters=${request.filters}, temporal=${request.temporal}")

        val query = queryBuilder.buildQuery(request)
        val sort = queryBuilder.buildSort(request)
        val from = request.pagination.page * request.pagination.size

        val response = client.search({
            it.index(props.index)
                .query(query)
                .sort(sort)
                .from(from)
                .size(request.pagination.size)
        }, IndexedMetadata::class.java)

        val items = response.hits().hits().mapNotNull { it.source() }

        log.info("Search result: total=${response.hits().total()?.value() ?: 0}, returned=${items.size}")

        return SearchResultPage(
            total = response.hits().total()?.value() ?: 0,
            items = items
        )
    }
}