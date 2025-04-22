package sorivma.geogeometryservice.application.service.api

import sorivma.geogeometryservice.application.dto.request.CreateGeometryRequest
import sorivma.geogeometryservice.application.dto.request.RollbackGeometryRequest
import sorivma.geogeometryservice.application.dto.request.UpdateGeometryRequest
import sorivma.geogeometryservice.application.dto.response.GeometryAuditLogDto
import sorivma.geogeometryservice.application.dto.response.GeometryDto
import java.util.*

interface GeometryService {
    fun create(request: CreateGeometryRequest): GeometryDto
    fun update(request: UpdateGeometryRequest): GeometryDto
    fun rollback(request: RollbackGeometryRequest): GeometryDto
    fun delete(objectId: UUID)
    fun getCurrent(objectId: UUID, format: String, targetSrid: Int? = null): GeometryDto
    fun getAllVersions(objectId: UUID, format: String, targetSrid: Int? = null): List<GeometryDto>
    fun getAuditLogs(objectId: UUID): List<GeometryAuditLogDto>
}