package sorivma.geosearchindexer.infrastructure.config.opensearch

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.hc.client5.http.auth.AuthScope
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider
import org.apache.hc.core5.http.HttpHost
import org.opensearch.client.json.jackson.JacksonJsonpMapper
import org.opensearch.client.opensearch.OpenSearchClient
import org.opensearch.client.transport.OpenSearchTransport
import org.opensearch.client.transport.httpclient5.ApacheHttpClient5TransportBuilder
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import sorivma.geosearchindexer.infrastructure.config.opensearch.properties.OpenSearchProperties

@Configuration
class OpensearchConfig(
    private val props: OpenSearchProperties,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(OpensearchConfig::class.java)

    @Bean
    fun opensearchClient(): OpenSearchClient {
        val host = HttpHost(props.scheme, props.host, props.port)
        val credentialsProvider = BasicCredentialsProvider().apply {
            setCredentials(
                AuthScope(host),
                UsernamePasswordCredentials(props.username, props.password.toCharArray())
            )
        }

        val transport: OpenSearchTransport = ApacheHttpClient5TransportBuilder.builder(host)
            .setHttpClientConfigCallback { httpClientBuilder ->
                httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
            }

            .setMapper(JacksonJsonpMapper(objectMapper))
            .build()

        val loggingTransport = LoggingTransport(transport)
        val client = OpenSearchClient(loggingTransport)

        try {
            val info = client.info()
            logger.info("Established connection to OpenSearch")
            logger.info("Cluster ${info.clusterName()}")
            logger.info("Version: ${info.version().number()}")
            logger.info("Distribution: ${info.version().distribution()}")
            logger.info("Node: ${info.name()}")
        } catch (e: Exception) {
            logger.error("Error during connection", e)
        }

        return client
    }
}