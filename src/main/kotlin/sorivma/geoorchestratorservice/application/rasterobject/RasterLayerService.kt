package sorivma.geoorchestratorservice.application.rasterobject

import com.fasterxml.jackson.databind.JsonNode
import sorivma.geoorchestratorservice.api.`object`.request.RasterUploadResponse
import sorivma.geoorchestratorservice.application.layer.dto.RasterLayerDataDto
import java.io.InputStream
import java.util.*

interface RasterLayerService {
    fun uploadFromStream(layerId: UUID, inputStream: InputStream): RasterUploadResponse
    fun getTileJson(layerId: UUID): JsonNode
    fun getData(layerId: UUID): RasterLayerDataDto
    fun delete(layerId: UUID)
}