package sorivma.geoorchestratorservice.api.`object`

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import sorivma.geoorchestratorservice.api.`object`.request.RasterUploadResponse
import sorivma.geoorchestratorservice.application.layer.dto.RasterLayerDataDto
import sorivma.geoorchestratorservice.application.rasterobject.RasterLayerService
import java.io.InputStream
import java.util.*

@RestController
@RequestMapping("/raster/layers/{layerId}")
class RasterObjectController(
    private val rasterLayerService: RasterLayerService
) {
    @PostMapping("/upload", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun uploadRasterData(
        @PathVariable("layerId") layerId: UUID,
        inputStream: InputStream
    ): ResponseEntity<RasterUploadResponse> {
        val data = rasterLayerService.uploadFromStream(layerId, inputStream)
        return ResponseEntity.status(HttpStatus.CREATED).body(data)
    }

    @GetMapping("/tilejson")
    fun getTileJson(@PathVariable layerId: UUID): JsonNode {
        return rasterLayerService.getTileJson(layerId)
    }

    @GetMapping("/tilestream")
    fun getRasterData(@PathVariable layerId: UUID): RasterLayerDataDto {
        return rasterLayerService.getData(layerId)
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteRasterData(@PathVariable layerId: UUID) {
        rasterLayerService.delete(layerId)
    }
}