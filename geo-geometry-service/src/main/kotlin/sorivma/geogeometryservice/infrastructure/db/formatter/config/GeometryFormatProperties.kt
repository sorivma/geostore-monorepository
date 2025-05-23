package sorivma.geogeometryservice.infrastructure.db.formatter.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("geometry.formats")
class GeometryFormatProperties {
    var dynamic: List<DynamicFormat> = emptyList()
}

data class DynamicFormat(
    val name: String,
    val toSqlExpression: String,
    val fromSQLExpression: String
)