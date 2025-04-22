package sorivma.geogeometryservice.application.service.api

import sorivma.geogeometryservice.api.spatial.dto.BoundingBox
import sorivma.geogeometryservice.api.spatial.dto.response.BoundingBoxResponse
import sorivma.geogeometryservice.api.spatial.dto.response.CrsResponse
import sorivma.geogeometryservice.api.spatial.dto.response.FilterByBoundingBoxResponse
import sorivma.geogeometryservice.application.dto.response.GeometryDto
import java.util.UUID

interface SpatialQueryService {
    fun getBoundingBoxes(objectIds: List<UUID>, targetSrid: Int? = null): List<BoundingBoxResponse>
    fun filterByBoundingBox(objectIds: List<UUID>, bbox: BoundingBox, bboxSrid: Int? = null): FilterByBoundingBoxResponse
    fun filterAndFetch(objectIds: List<UUID>, bbox: BoundingBox, format: String, srid: Int?): List<GeometryDto>
    fun getCrs(objectIds: List<UUID>): List<CrsResponse>
}