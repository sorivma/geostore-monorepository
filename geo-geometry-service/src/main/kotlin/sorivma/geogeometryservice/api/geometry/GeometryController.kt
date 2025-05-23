package sorivma.geogeometryservice.api.geometry

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import sorivma.geogeometryservice.application.dto.request.RollbackGeometryRequest
import sorivma.geogeometryservice.application.dto.response.GeometryAuditLogDto
import sorivma.geogeometryservice.application.dto.response.GeometryDto
import sorivma.geogeometryservice.application.service.api.GeometryService
import sorivma.geogeometryservice.api.geometry.dto.mapper.GeometryRequestMapper
import sorivma.geogeometryservice.api.geometry.dto.request.CreateGeometryWebRequest
import sorivma.geogeometryservice.api.geometry.dto.request.UpdateGeometryWebRequest
import sorivma.geogeometryservice.domain.exception.GeometryNotFoundException
import java.util.*

@RestController
@RequestMapping("/geometries")
class GeometryController(
    private val geometryService: GeometryService,
    private val geometryRequestMapper: GeometryRequestMapper
) {
    private final val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @RequestBody body: CreateGeometryWebRequest,
        @RequestParam format: String,
        @RequestParam srid: Int? = null
    ): GeometryDto {
        val request = geometryRequestMapper.toCreateRequest(body, format, srid)
        return geometryService.create(request)
    }

    @PutMapping("/{objectId}")
    fun update(
        @PathVariable objectId: UUID,
        @RequestBody body: UpdateGeometryWebRequest,
        @RequestParam format: String,
        @RequestParam srid: Int? = null
    ): GeometryDto {
        val request = geometryRequestMapper.toUpdateRequest(objectId, body, format, srid)
        return geometryService.update(request)
    }

    @DeleteMapping("/{objectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable objectId: UUID) {
        geometryService.delete(objectId)
    }

    @PostMapping("/{objectId}/rollback")
    fun rollback(
        @PathVariable objectId: UUID,
        @RequestBody body: RollbackGeometryRequest,
    ): GeometryDto {
        return geometryService.rollback(body)
    }

    @GetMapping("/{objectId}/current")
    fun getCurrent(
        @PathVariable objectId: UUID,
        @RequestParam format: String,
        @RequestParam srid: Int? = null
    ): GeometryDto {
        return geometryService.getCurrent(objectId, format, srid)
    }

    @PostMapping("/batch")
    fun getCurrent(
        @RequestBody objectIds: List<UUID>,
        @RequestParam format: String,
        @RequestParam srid: Int? = null
    ): List<GeometryDto> {
        return objectIds.mapNotNull {
            try {
                geometryService.getCurrent(it, format, srid)
            } catch (e: GeometryNotFoundException) {
                logger.warn(e.message)
                null
            }
        }
    }

    @GetMapping("/{objectId}/versions")
    fun getAllVersions(
        @PathVariable objectId: UUID,
        @RequestParam format: String,
        @RequestParam srid: Int? = null
    ): List<GeometryDto> {
        return geometryService.getAllVersions(objectId, format, srid)
    }

    @GetMapping("/{objectId}/audit")
    fun getAuditLogs(@PathVariable objectId: UUID): List<GeometryAuditLogDto> {
        return geometryService.getAuditLogs(objectId)
    }
}