package sorivma.geogeometryservice.infrastructure.db.formatter.registry.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sorivma.geogeometryservice.infrastructure.db.formatter.config.GeometryFormatProperties
import sorivma.geogeometryservice.infrastructure.db.formatter.exception.UnsupportedFormatException
import sorivma.geogeometryservice.infrastructure.db.formatter.formatter.GeometrySqlFormatter
import sorivma.geogeometryservice.infrastructure.db.formatter.formatter.impl.ConfigurableFormatter
import sorivma.geogeometryservice.infrastructure.db.formatter.registry.GeometryFormatRegistry

@Component
class InMemoryGeometryFormatRegistry(
    staticFormatters: List<GeometrySqlFormatter>,
    props: GeometryFormatProperties
): GeometryFormatRegistry {

    private val log = LoggerFactory.getLogger(InMemoryGeometryFormatRegistry::class.java)

    private val formatterMap: Map<String, GeometrySqlFormatter> = buildMap {
        staticFormatters.forEach {
            val key = it.formatName().lowercase()
            put(key, it)
            log.info("Registered static formatter: $key → ${it.javaClass.simpleName}")
        }

        props.dynamic.forEach {
            val key = it.name.lowercase()
            val formatter = ConfigurableFormatter(
                name = it.name,
                toSqlTemplate = it.toSqlExpression,
                fromSqlTemplate = it.fromSQLExpression
            )
            put(key, formatter)
            log.info("Registered dynamic formatter: $key → [sql=${it.toSqlExpression}]")
        }
    }

    override fun get(format: String): GeometrySqlFormatter =
        formatterMap[format.lowercase()]
            ?: throw UnsupportedFormatException(format)

    override fun supportedFormats(): Set<String> = formatterMap.keys
}