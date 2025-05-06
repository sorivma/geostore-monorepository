package sorivma.geoorchestratorservice.application.vectorobject

import sorivma.geoorchestratorservice.application.vectorobject.dto.SearchResultDto
import sorivma.geoorchestratorservice.infrastructure.query.dto.*
import java.util.UUID

interface VectorObjectSearchService {
    fun searchInLayer(
        layerId: UUID,
        query: String,
        filters: SearchFilters? = null,
        temporal: TemporalFilter? = null,
        pagination: PageRequest = PageRequest(),
        sort: SortRequest = SortRequest()
    ): List<SearchResultDto>

    fun searchInProject(
        projectId: UUID,
        query: String,
        filters: SearchFilters? = null,
        temporal: TemporalFilter? = null,
        pagination: PageRequest = PageRequest(),
        sort: SortRequest = SortRequest()
    ): List<SearchResultDto>
}