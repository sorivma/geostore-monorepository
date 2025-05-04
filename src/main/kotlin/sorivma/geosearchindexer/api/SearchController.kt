package sorivma.geosearchindexer.api

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import sorivma.geosearchindexer.api.dto.SearchQueryRequest
import sorivma.geosearchindexer.api.dto.SearchResponse
import sorivma.geosearchindexer.application.service.SearchService
import sorivma.geosearchindexer.domain.model.IndexedMetadata

@RestController
@RequestMapping("/search")
class SearchController(
    private val searchService: SearchService,
) {
    @PostMapping
    fun search(@RequestBody request: SearchQueryRequest): SearchResponse<IndexedMetadata> {
        val resultPage = searchService.search(request)

        return SearchResponse(
            total = resultPage.total,
            page = request.pagination.page,
            size = request.pagination.size,
            items = resultPage.items,
        )
    }
}