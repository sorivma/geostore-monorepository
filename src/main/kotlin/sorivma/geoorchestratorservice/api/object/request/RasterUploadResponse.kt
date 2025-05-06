package sorivma.geoorchestratorservice.api.`object`.request

import com.fasterxml.jackson.databind.JsonNode
import java.util.*

data class RasterUploadResponse(
    val layerId: UUID,
    val cogUrl: String,
    val tileJsonUrl: String,
)