package sorivma.geoorchestratorservice.application.vectorobject.dto

import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*

data class VectorObjectDto(
    val objectId: UUID,
    val geometry: Any,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val metadata: Map<String, Any?>? = null
)
