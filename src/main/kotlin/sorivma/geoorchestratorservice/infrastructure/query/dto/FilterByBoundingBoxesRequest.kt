package sorivma.geoorchestratorservice.infrastructure.query.dto

import sorivma.geoorchestratorservice.application.vectorobject.dto.BboxDto

data class FilterByBoundingBoxesRequest(
    val objectIds: List<String>,
    val bbox: BboxDto,
)
