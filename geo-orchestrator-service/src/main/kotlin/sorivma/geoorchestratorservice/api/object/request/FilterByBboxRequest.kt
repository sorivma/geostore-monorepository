package sorivma.geoorchestratorservice.api.`object`.request

import sorivma.geoorchestratorservice.application.vectorobject.dto.BboxDto

data class FilterByBboxRequest(
    val bbox: BboxDto,
    val includeMetadata: Boolean = false,
    val srid: Int = 4326,
    val format: String = "geojson"
)
