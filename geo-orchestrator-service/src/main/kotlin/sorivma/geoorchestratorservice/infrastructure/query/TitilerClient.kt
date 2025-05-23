package sorivma.geoorchestratorservice.infrastructure.query

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "titiler", url = "\${services.titiler.url}")
interface TitilerClient {
    @GetMapping("/cog/{tileMatrixSetId}/tilejson.json")
    fun getTileJson(
        @PathVariable tileMatrixSetId: String,
        @RequestParam url: String
    ): JsonNode
}