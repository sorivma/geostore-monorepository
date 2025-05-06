package sorivma.geoorchestratorservice.config.minio

import io.minio.MinioClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MinioClientConfig {
    @Bean
    fun minioClient(props: MinioProps): MinioClient {
        return MinioClient.builder()
            .endpoint(props.endpoint)
            .credentials(props.accessKey, props.secretKey)
            .build()
    }
}