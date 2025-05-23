package sorivma.geogeometryservice.infrastructure.db.formatter.formatter.impl

import sorivma.geogeometryservice.infrastructure.db.formatter.formatter.GeometrySqlFormatter

abstract class BaseGeometryFormatter : GeometrySqlFormatter {

    override fun toSqlExpression(column: String, srid: Int): String {
        val base = if (srid != 3857) "ST_Transform($column, $srid)" else column
        return wrapOutputSql(base)
    }

    override fun convertRaw(dbValue: Any): Any = dbValue

    protected abstract fun wrapOutputSql(column: String): String
}