package sorivma.geoorchestratorservice.infrastructure.query

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*
import sorivma.geoorchestratorservice.infrastructure.query.dto.MetadataDto
import java.util.*

@FeignClient(
    name = "metadata-service",
    url = "\${services.metadata-service.url}",
)
interface MetadataQueryClient {
    @PostMapping("metadata/batch")
    fun getByObjectIds(
        @RequestBody objectIds: List<UUID>,
        @RequestHeader("Authorization") authHeader: String
    ): List<MetadataDto>

    @GetMapping("/metadata/{objectId}")
    fun getByObjectId(
        @PathVariable objectId: UUID,
        @RequestHeader("Authorization") authHeader: String
    ): MetadataDto
}