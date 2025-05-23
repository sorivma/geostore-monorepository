package sorivma.geogeometryservice.infrastructure.db.formatter.formatter.impl

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import sorivma.geogeometryservice.config.geo.GeoProperties
import sorivma.geogeometryservice.infrastructure.db.formatter.formatter.GeometrySqlFormatter

@Component
class GeoJsonFormatter(
    private val objectMapper: ObjectMapper
) : BaseGeometryFormatter() {

    override fun formatName(): String = "geojson"

    override fun fromSqlExpression(paramName: String, inputSrid: Int, targetSrid: Int): String {
        val parsed = "ST_GeomFromGeoJSON(:$paramName)"
        val withInputSrid = "ST_SetSRID($parsed, $inputSrid)"
        return if (inputSrid != targetSrid)
            "ST_Transform($withInputSrid, $targetSrid)"
        else
            withInputSrid
    }

    override fun wrapOutputSql(column: String): String = "ST_AsGeoJSON($column)"

    override fun convertRaw(dbValue: Any): Any {
        val json = dbValue as? String
            ?: throw IllegalArgumentException("Expected JSON string, got ${dbValue::class.simpleName}")
        return objectMapper.readTree(json)
    }
}