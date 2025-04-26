package sorivma.geogeometryservice.infrastructure.db.formatter.formatter.impl

import org.springframework.stereotype.Component

@Component
class WkbFormatter : BaseGeometryFormatter() {

    override fun formatName(): String = "wkb"

    override fun fromSqlExpression(paramName: String, inputSrid: Int, targetSrid: Int): String {
        val parsed = "ST_GeomFromWKB(:$paramName)"
        val withInputSrid = "ST_SetSRID($parsed, $inputSrid)"
        return if (inputSrid != targetSrid)
            "ST_Transform($withInputSrid, $targetSrid)"
        else
            withInputSrid
    }

    override fun wrapOutputSql(column: String): String = "ST_AsBinary($column)"

    override fun convertRaw(dbValue: Any): Any {
        return dbValue as? ByteArray
            ?: throw IllegalArgumentException("Expected ByteArray for WKB, got: ${dbValue::class.simpleName}")
    }
}