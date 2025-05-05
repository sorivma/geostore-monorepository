package sorivma.geoorchestratorservice.infrastructure.query

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*
import sorivma.geoorchestratorservice.infrastructure.query.dto.*
import java.util.*


@FeignClient(
    name = "geometry-service",
    url = "\${services.geometry-service.url}",
)
interface GeometryQueryClient {
    @PostMapping("/spatial/filter-and-fetch-bbox")
    fun filterAndFetchBbox(
        @RequestBody request: FilterByBoundingBoxesRequest,
        @RequestParam srid: Int,
        @RequestParam format: String,
        @RequestHeader("Authorization") authHeader: String
    ): List<GeometryDto>

    @PostMapping("/geometries/batch")
    fun getByObjectIds(
        @RequestBody objectIds: List<UUID>,
        @RequestParam format: String,
        @RequestParam srid: Int,
        @RequestHeader("Authorization") authHeader: String
    ): List<GeometryDto>

    @GetMapping("/geometries/{objectId}/current")
    fun getCurrent(
        @PathVariable objectId: UUID,
        @RequestParam format: String,
        @RequestParam srid: Int?,
        @RequestHeader("Authorization") authHeader: String
    ): GeometryDto

    @PostMapping("/geometries/batch-current")
    fun getCurrentBatch(
        @RequestBody objectIds: List<UUID>,
        @RequestParam format: String,
        @RequestParam srid: Int?,
        @RequestHeader("Authorization") authHeader: String
    ): List<GeometryDto>

    @GetMapping("/geometries/{objectId}/versions")
    fun getAllVersions(
        @PathVariable objectId: UUID,
        @RequestParam format: String,
        @RequestParam srid: Int?,
        @RequestHeader("Authorization") authHeader: String
    ): List<GeometryDto>

    @GetMapping("/geometries/{objectId}/audit")
    fun getAuditLogs(
        @PathVariable objectId: UUID,
        @RequestHeader("Authorization") authHeader: String
    ): List<GeometryAuditLogDto>

    @PostMapping("/geometries/bounds")
    fun getObjectsBounds(
        @RequestBody request: BoundingBoxesRequest,
        @RequestParam srid: Int?,
        @RequestHeader("Authorization") authHeader: String
    ): List<BoundingBoxResponse>

    @PostMapping("/geometries/crs")
    fun getCrs(
        @RequestBody request: CrsRequest,
        @RequestHeader("Authorization") authHeader: String
    ): List<CrsResponse>
}