package sorivma.geosearchindexer.infrastructure.config.opensearch.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "opensearch")
class OpenSearchProperties {
    var scheme: String = "http"
    lateinit var host: String
    var port: Int = 9200
    lateinit var username: String
    lateinit var password: String
    lateinit var index: String
    var initialize: Boolean = true
}
