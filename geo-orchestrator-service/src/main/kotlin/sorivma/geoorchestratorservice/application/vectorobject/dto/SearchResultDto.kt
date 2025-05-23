package sorivma.geoorchestratorservice.application.vectorobject.dto

import java.util.*

data class SearchResultDto(
    val layerId: UUID,
    val projectId: UUID,
    val geoJsonGeometry: Any,
    val anyText: String,
)