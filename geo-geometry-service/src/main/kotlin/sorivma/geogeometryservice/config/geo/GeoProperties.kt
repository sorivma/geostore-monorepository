package sorivma.geogeometryservice.config.geo

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "geo")
@Configuration
data class GeoProperties(
    var defaultSrid: Int = 3857
)
