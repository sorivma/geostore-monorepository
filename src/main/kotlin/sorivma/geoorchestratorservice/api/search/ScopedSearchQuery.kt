package sorivma.geoorchestratorservice.api.search

import sorivma.geoorchestratorservice.infrastructure.query.dto.PageRequest
import sorivma.geoorchestratorservice.infrastructure.query.dto.SearchFilters
import sorivma.geoorchestratorservice.infrastructure.query.dto.SortRequest
import sorivma.geoorchestratorservice.infrastructure.query.dto.TemporalFilter

data class ScopedSearchQuery(
    val query: String,
    val filters: SearchFilters? = null,
    val temporal: TemporalFilter? = null,
    val pagination: PageRequest = PageRequest(),
    val sort: SortRequest = SortRequest()
)
