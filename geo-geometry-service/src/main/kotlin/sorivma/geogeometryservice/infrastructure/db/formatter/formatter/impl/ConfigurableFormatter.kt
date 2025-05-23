package sorivma.geogeometryservice.infrastructure.db.formatter.formatter.impl

class ConfigurableFormatter(
    private val name: String,
    private val toSqlTemplate: String,
    private val fromSqlTemplate: String
) : BaseGeometryFormatter() {

    override fun formatName(): String = name

    override fun fromSqlExpression(paramName: String, inputSrid: Int, targetSrid: Int): String {
        val parsed = fromSqlTemplate.replace("{param}", paramName)
        val withInputSrid = "ST_SetSRID($parsed, $inputSrid)"
        return if (inputSrid != targetSrid)
            "ST_Transform($withInputSrid, $targetSrid)"
        else
            withInputSrid
    }

    override fun wrapOutputSql(column: String): String =
        toSqlTemplate.replace("{column}", column)
}