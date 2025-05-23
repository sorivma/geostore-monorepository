package sorivma.geoorchestratorservice.api.`object`

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import sorivma.geoorchestratorservice.api.`object`.request.FilterByBboxRequest
import sorivma.geoorchestratorservice.application.vectorobject.VectorObjectQueryService
import sorivma.geoorchestratorservice.application.vectorobject.VectorObjectService
import sorivma.geoorchestratorservice.application.vectorobject.dto.CreateVectorObjectRequest
import sorivma.geoorchestratorservice.application.vectorobject.dto.VectorObjectDto
import java.util.*

@RestController
@RequestMapping("/vector/layers/{layerId}/objects")
class VectorObjectController(
    private val queryService: VectorObjectQueryService,
    private val commandService: VectorObjectService,
) {

    @GetMapping
    fun getAll(
        @PathVariable layerId: UUID,
        @RequestParam(defaultValue = "false") includeMetadata: Boolean,
        @RequestParam(defaultValue = "geojson") format: String,
        @RequestParam(defaultValue = "4326") srid: Int
    ): List<VectorObjectDto> {
        return queryService.getAll(
            layerId = layerId,
            includeMetadata = includeMetadata,
            format = format,
            srid = srid
        )
    }

    @PostMapping("/filter-bbox")
    fun getByBbox(
        @PathVariable layerId: UUID,
        @RequestBody request: FilterByBboxRequest
    ): List<VectorObjectDto> {
        return queryService.getByBbox(
            layerId = layerId,
            bbox = request.bbox,
            srid = request.srid,
            includeMetadata = request.includeMetadata,
            format = request.format
        )
    }

    @PostMapping
    fun create(
        @PathVariable layerId: UUID,
        @RequestBody request: CreateVectorObjectRequest
    ): ResponseEntity<UUID> {
        val objectId = commandService.create(layerId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(objectId)
    }

    @PutMapping("/{objectId}")
    fun update(
        @PathVariable layerId: UUID,
        @PathVariable objectId: UUID,
        @RequestBody request: CreateVectorObjectRequest
    ) {
        commandService.update(layerId, objectId, request)
    }

    @DeleteMapping("/{objectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable layerId: UUID,
        @PathVariable objectId: UUID
    ) {
        commandService.delete(layerId, objectId)
    }

    @GetMapping("/{objectId}")
    fun getByObjectId(
        @PathVariable layerId: UUID,
        @PathVariable objectId: UUID,
        @RequestParam(defaultValue = "false") includeMetadata: Boolean,
        @RequestParam(defaultValue = "geojson") format: String,
        @RequestParam(defaultValue = "4326") srid: Int
    ): VectorObjectDto {
        return queryService.getByObjectId(
            layerId = layerId,
            objectId = objectId,
            includeMetadata = includeMetadata,
            format = format,
            srid = srid
        )
    }

    @GetMapping("/formatted/geojson", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getFormattedGeoJson(
        @PathVariable layerId: UUID,
        @RequestParam(defaultValue = "4326") srid: Int,
        @RequestParam(defaultValue = "true") includeMetadata: Boolean
    ): Map<String, Any> {
        val objects = queryService.getAll(layerId, includeMetadata, "GEOJSON", srid)

        return mapOf(
            "type" to "FeatureCollection",
            "features" to objects.map {
                mapOf(
                    "type" to "Feature",
                    "id" to it.objectId.toString(),
                    "geometry" to it.geometry,
                    "properties" to buildProperties(it)
                )
            }
        )
    }

    @GetMapping("/formatted/wkt", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun getFormattedWkt(
        @PathVariable layerId: UUID,
        @RequestParam(defaultValue = "4326") srid: Int
    ): ResponseEntity<String> {
        val objects = queryService.getAll(layerId, includeMetadata = false, format = "WKT", srid = srid)

        val result = objects.joinToString(separator = "\n") {
            it.geometry.toString()
        }

        return ResponseEntity.ok(result)
    }

    @GetMapping("/formatted/wkb", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun getFormattedWkb(
        @PathVariable layerId: UUID,
        @RequestParam(defaultValue = "4326") srid: Int
    ): ResponseEntity<ByteArray> {
        val objects = queryService.getAll(layerId, includeMetadata = false, format = "WKB", srid = srid)

        val binary: ByteArray = objects
            .map { it.geometry }
            .filterIsInstance<String>()
            .flatMap { Base64.getDecoder().decode(it).asIterable() }
            .toByteArray()

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"layer_$layerId.wkb\"")
            .body(binary)
    }


    private fun buildProperties(obj: VectorObjectDto): Map<String, Any?> =
        mapOf("objectId" to obj.objectId) + (obj.metadata ?: emptyMap())
}