package sorivma.geogeometryservice.api.spatial

import org.springframework.web.bind.annotation.*
import sorivma.geogeometryservice.api.spatial.dto.request.BoundingBoxesRequest
import sorivma.geogeometryservice.api.spatial.dto.request.CrsRequest
import sorivma.geogeometryservice.api.spatial.dto.request.FilterByBoundingBoxesRequest
import sorivma.geogeometryservice.api.spatial.dto.response.BoundingBoxResponse
import sorivma.geogeometryservice.api.spatial.dto.response.CrsResponse
import sorivma.geogeometryservice.api.spatial.dto.response.FilterByBoundingBoxResponse
import sorivma.geogeometryservice.application.dto.response.GeometryDto
import sorivma.geogeometryservice.application.service.api.SpatialQueryService
import java.util.*

@RestController
@RequestMapping("/spatial")
class SpatialController(
    private val spatialService: SpatialQueryService
) {
    @PostMapping("/bounds")
    fun getObjectsBounds(
        @RequestBody request: BoundingBoxesRequest,
        @RequestParam srid: Int?,
    ): List<BoundingBoxResponse> {
        return spatialService.getBoundingBoxes(request.objectIds.map { UUID.fromString(it) }, srid)
    }

    @PostMapping("/filter-by-bbox")
    fun filterByBbox(
        @RequestBody request: FilterByBoundingBoxesRequest,
        @RequestParam srid: Int?,
    ): FilterByBoundingBoxResponse {
        return spatialService.filterByBoundingBox(request.objectIds.map { UUID.fromString(it) }, request.bbox, srid)
    }

    @PostMapping("/filter-and-fetch-bbox")
    fun filterAndFetchByBbox(
        @RequestBody request: FilterByBoundingBoxesRequest,
        @RequestParam srid: Int?,
        @RequestParam format: String,
    ): List<GeometryDto> {
        return spatialService.filterAndFetch(
            request.objectIds.map { UUID.fromString(it) },
            request.bbox,
            format,
            srid
        )
    }

    @PostMapping("/crs")
    fun getCrs(
        @RequestBody request: CrsRequest
    ): List<CrsResponse> {
        return spatialService.getCrs(request.objectIds.map { UUID.fromString(it) })
    }
}