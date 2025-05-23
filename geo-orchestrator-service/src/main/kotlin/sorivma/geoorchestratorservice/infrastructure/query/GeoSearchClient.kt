package sorivma.geoorchestratorservice.infrastructure.query

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import sorivma.geoorchestratorservice.infrastructure.query.dto.IndexedMetadata
import sorivma.geoorchestratorservice.infrastructure.query.dto.SearchQueryRequest
import sorivma.geoorchestratorservice.infrastructure.query.dto.SearchResponse

@FeignClient(name = "geo-search", url = "\${services.search-service.url}")
interface GeoSearchClient {
    @PostMapping("/search")
    fun search(
        @RequestBody request: SearchQueryRequest,
        @RequestHeader("Authorization") authHeader: String
    ): SearchResponse<IndexedMetadata>
}