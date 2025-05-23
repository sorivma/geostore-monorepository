package sorivma.geogeometryservice.infrastructure.db.formatter.formatter.impl

import org.springframework.stereotype.Component

@Component
class WktFormatter : BaseGeometryFormatter() {

    override fun formatName(): String = "wkt"

    override fun fromSqlExpression(paramName: String, inputSrid: Int, targetSrid: Int): String {
        val parsed = "ST_GeomFromText(:$paramName)"
        val withInputSrid = "ST_SetSRID($parsed, $inputSrid)"
        return if (inputSrid != targetSrid)
            "ST_Transform($withInputSrid, $targetSrid)"
        else
            withInputSrid
    }

    override fun wrapOutputSql(column: String): String = "ST_AsText($column)"

    override fun convertRaw(dbValue: Any): Any {
        return dbValue as? String
            ?: throw IllegalArgumentException("Expected String from DB for WKT, got: ${dbValue::class.simpleName}")
    }
}