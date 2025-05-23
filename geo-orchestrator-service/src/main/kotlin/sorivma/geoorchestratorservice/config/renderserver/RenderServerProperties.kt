package sorivma.geoorchestratorservice.config.renderserver

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "render")
class RenderServerProperties {
    lateinit var baseUrl: String
    lateinit var tileMatrixSetId: String
}