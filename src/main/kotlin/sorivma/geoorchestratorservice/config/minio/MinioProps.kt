package sorivma.geoorchestratorservice.config.minio

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "minio")
class MinioProps {
    lateinit var endpoint: String
    lateinit var bucket: String
    lateinit var accessKey: String
    lateinit var secretKey: String
}