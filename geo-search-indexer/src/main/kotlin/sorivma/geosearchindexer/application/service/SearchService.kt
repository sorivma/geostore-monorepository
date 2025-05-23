package sorivma.geosearchindexer.application.service

import sorivma.geosearchindexer.api.dto.SearchQueryRequest
import sorivma.geosearchindexer.application.service.dto.SearchResultPage
import sorivma.geosearchindexer.domain.model.IndexedMetadata

interface SearchService {
    fun search(request: SearchQueryRequest): SearchResultPage<IndexedMetadata>
}