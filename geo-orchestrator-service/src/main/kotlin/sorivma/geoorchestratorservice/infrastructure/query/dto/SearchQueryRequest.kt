package sorivma.geoorchestratorservice.infrastructure.query.dto

import java.util.UUID

data class SearchQueryRequest(
    val objectIds: List<UUID>,
    val query: String,
    val filters: SearchFilters? = null,
    val temporal: TemporalFilter? = null,
    val pagination: PageRequest = PageRequest(),
    val sort: SortRequest = SortRequest()
)
