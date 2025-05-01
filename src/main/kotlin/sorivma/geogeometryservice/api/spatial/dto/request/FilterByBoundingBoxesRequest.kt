package sorivma.geogeometryservice.api.spatial.dto.request

import sorivma.geogeometryservice.api.spatial.dto.BoundingBox

data class FilterByBoundingBoxesRequest(
    val objectIds: List<String>,
    val bbox: BoundingBox
)