package sorivma.geometadataservice.api.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import sorivma.geometadataservice.api.dto.MetadataDto
import sorivma.geometadataservice.api.mapper.toDomain
import sorivma.geometadataservice.api.mapper.toDto
import sorivma.geometadataservice.application.geometadata.MetadataService
import java.util.*

@RestController
@RequestMapping("/metadata")
class MetadataController(
    private val metadataService: MetadataService,
) {
    @PostMapping
    fun create(@RequestBody data: MetadataDto): ResponseEntity<Void> {
        metadataService.create(data.toDomain())
        return ResponseEntity.ok().build()
    }

    @GetMapping("/{objectId}")
    fun get(@PathVariable objectId: UUID): ResponseEntity<MetadataDto> {
        val metadata = metadataService.getByObjectId(objectId)
        return ResponseEntity.ok().body(metadata.toDto())
    }

    @PostMapping("/batch")
    fun getByIds(@RequestBody objectIds: List<UUID>): ResponseEntity<List<MetadataDto>> {
        return ResponseEntity.ok().body(objectIds.map { metadataService.getByObjectId(it).toDto() })
    }

    @PutMapping("/{objectId}")
    fun update(@PathVariable objectId: UUID, @RequestBody data: MetadataDto): ResponseEntity<Void> {
        metadataService.update(objectId, data.toDomain())
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{objectId}")
    fun delete(@PathVariable objectId: UUID): ResponseEntity<Void> {
        metadataService.delete(objectId)
        return ResponseEntity.ok().build()
    }
}