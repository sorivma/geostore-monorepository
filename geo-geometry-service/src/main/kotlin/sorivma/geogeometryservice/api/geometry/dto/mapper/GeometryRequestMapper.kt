package sorivma.geogeometryservice.api.geometry.dto.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import sorivma.geogeometryservice.application.dto.request.CreateGeometryRequest
import sorivma.geogeometryservice.application.dto.request.UpdateGeometryRequest
import sorivma.geogeometryservice.api.geometry.dto.request.CreateGeometryWebRequest
import sorivma.geogeometryservice.api.geometry.dto.request.UpdateGeometryWebRequest
import sorivma.geogeometryservice.config.geo.GeoProperties
import java.util.*

@Component
class GeometryRequestMapper(
    private val objectMapper: ObjectMapper,
    private val geoProperties: GeoProperties
) {
    fun toCreateRequest(
        body: CreateGeometryWebRequest,
        format: String,
        sourceSrid: Int? = null
    ): CreateGeometryRequest {
        val serialized = serializeGeometry(body.geometry)
        return CreateGeometryRequest(body.objectId, serialized, format, sourceSrid ?: geoProperties.defaultSrid)
    }

    fun toUpdateRequest(
        objectId: UUID,
        body: UpdateGeometryWebRequest,
        format: String,
        sourceSrid: Int? = null
    ): UpdateGeometryRequest {
        val serialized = serializeGeometry(body.newGeometry)
        return UpdateGeometryRequest(objectId, serialized, format, sourceSrid ?: geoProperties.defaultSrid)
    }

    private fun serializeGeometry(geometry: Any): String {
        return when (geometry) {
            is String -> geometry
            else -> objectMapper.writeValueAsString(geometry)
        }
    }
}