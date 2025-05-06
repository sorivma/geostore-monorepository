package sorivma.geoorchestratorservice.api.layer

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import sorivma.geoorchestratorservice.api.layer.request.CreateLayerRequest
import sorivma.geoorchestratorservice.application.layer.LayerService
import sorivma.geoorchestratorservice.application.layer.dto.CreateLayerDto
import sorivma.geoorchestratorservice.application.layer.dto.LayerResponse
import sorivma.geoorchestratorservice.application.layer.dto.UpdateLayerDto
import java.util.*

@RestController
@RequestMapping("/projects/{projectId}/layers")
class LayerController(
    private val layerService: LayerService
) {

    @PostMapping
    fun create(
        @PathVariable projectId: UUID,
        @RequestBody request: CreateLayerRequest
    ): ResponseEntity<Map<String, UUID>> {
        val layer = CreateLayerDto(
            projectId = projectId,
            name = request.name,
            type = request.type,
            order = request.order,
            geometryType = request.geometryType
        )

        val layerId = layerService.create(layer)
        return ResponseEntity.status(HttpStatus.CREATED).body(mapOf("id" to layerId))
    }

    @GetMapping
    fun getAll(@PathVariable projectId: UUID): List<LayerResponse> =
        layerService.getByProjectId(projectId)

    @GetMapping("/{layerId}")
    fun getById(
        @PathVariable projectId: UUID,
        @PathVariable layerId: UUID
    ): LayerResponse {
        return layerService.getById(layerId)
    }

    @PutMapping("/{layerId}")
    @ResponseStatus(HttpStatus.OK)
    fun update(
        @PathVariable projectId: UUID,
        @PathVariable layerId: UUID,
        @RequestBody request: UpdateLayerDto
    ) {
        layerService.update(layerId, request)
    }

    @DeleteMapping("/{layerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable projectId: UUID,
        @PathVariable layerId: UUID
    ) {
        layerService.deleteById(layerId)
    }
}