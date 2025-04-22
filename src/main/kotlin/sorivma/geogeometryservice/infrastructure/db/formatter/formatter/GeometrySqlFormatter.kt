package sorivma.geogeometryservice.infrastructure.db.formatter.formatter

interface GeometrySqlFormatter {
    fun formatName(): String
    fun toSqlExpression(column: String = "geometry", srid: Int): String
    fun fromSqlExpression(paramName: String, inputSrid: Int, targetSrid: Int): String
    fun convertRaw(dbValue: Any): Any
}