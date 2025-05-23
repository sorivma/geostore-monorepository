package sorivma.geoorchestratorservice.infrastructure.query.dto

import sorivma.geoorchestratorservice.application.vectorobject.dto.BboxDto

data class BoundingBoxResponse(
    val objectId: String,
    val srid: Int,
    val bbox: BboxDto
)