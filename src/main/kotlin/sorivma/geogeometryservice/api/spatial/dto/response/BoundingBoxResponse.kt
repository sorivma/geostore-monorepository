package sorivma.geogeometryservice.api.spatial.dto.response

import sorivma.geogeometryservice.api.spatial.dto.BoundingBox

data class BoundingBoxResponse(
    val objectId: String,
    val srid: Int,
    val bbox: BoundingBox
)