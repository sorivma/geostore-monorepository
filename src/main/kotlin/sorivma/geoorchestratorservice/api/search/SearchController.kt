package sorivma.geoorchestratorservice.api.search

import org.springframework.web.bind.annotation.*
import sorivma.geoorchestratorservice.application.vectorobject.VectorObjectSearchService
import sorivma.geoorchestratorservice.application.vectorobject.dto.SearchResultDto
import sorivma.geoorchestratorservice.infrastructure.query.dto.SearchResponse
import java.util.*

@RestController
@RequestMapping("/search")
class SearchController(
    private val searchService: VectorObjectSearchService
) {

    @PostMapping("/project/{projectId}")
    fun searchInProject(
        @PathVariable projectId: UUID,
        @RequestBody request: ScopedSearchQuery
    ): List<SearchResultDto> {
        return searchService.searchInProject(
            projectId = projectId,
            query = request.query,
            filters = request.filters,
            temporal = request.temporal,
            pagination = request.pagination,
            sort = request.sort
        )
    }

    @PostMapping("/layer/{layerId}")
    fun searchInLayer(
        @PathVariable layerId: UUID,
        @RequestBody request: ScopedSearchQuery
    ): List<SearchResultDto> {
        return searchService.searchInLayer(
            layerId = layerId,
            query = request.query,
            filters = request.filters,
            temporal = request.temporal,
            pagination = request.pagination,
            sort = request.sort
        )
    }
}