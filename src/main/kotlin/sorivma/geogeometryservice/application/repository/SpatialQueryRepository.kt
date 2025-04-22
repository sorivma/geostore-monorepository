package sorivma.geogeometryservice.application.repository

import sorivma.geogeometryservice.api.spatial.dto.BoundingBox
import sorivma.geogeometryservice.api.spatial.dto.response.BoundingBoxResponse
import sorivma.geogeometryservice.api.spatial.dto.response.CrsResponse
import java.util.*

interface SpatialQueryRepository {
    fun findBoundingBoxes(objectIds: List<UUID>, srid: Int): List<BoundingBoxResponse>
    fun findIntersectingObjectIds(objectIds: List<UUID>, bbox: BoundingBox, bboxSrid: Int, targetSrid: Int): List<String>
    fun findCrsByObjectIds(objectIds: List<UUID>): List<CrsResponse>
}