package sorivma.geoorchestratorservice.application.vectorobject.dto

import sorivma.geoorchestratorservice.infrastructure.messaging.publishing.dto.TemporalExtentMessageDto

data class CreateVectorObjectRequest(
    val geometry: Any,
    val format: String = "geojson",
    val sourceSrid: Int = 4326,
    val temporalExtent: TemporalExtentMessageDto? = null,
    val region: String? = null,
    val topicCategory: List<String>? = null,
    val properties: Map<String, Any>? = null
)
