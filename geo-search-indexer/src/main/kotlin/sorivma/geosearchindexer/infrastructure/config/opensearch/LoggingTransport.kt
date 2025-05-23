package sorivma.geosearchindexer.infrastructure.config.opensearch

import jakarta.json.stream.JsonGenerator
import org.opensearch.client.json.JsonpMapper
import org.opensearch.client.transport.Endpoint
import org.opensearch.client.transport.OpenSearchTransport
import org.opensearch.client.transport.TransportOptions
import org.slf4j.LoggerFactory
import java.io.StringWriter
import java.util.concurrent.CompletableFuture

class LoggingTransport(
    private val delegate: OpenSearchTransport
) : OpenSearchTransport by delegate {

    private val log = LoggerFactory.getLogger(LoggingTransport::class.java)

    override fun <RequestT, ResponseT, ErrorT> performRequest(
        request: RequestT,
        endpoint: Endpoint<RequestT, ResponseT, ErrorT>,
        options: TransportOptions?
    ): ResponseT {
        try {
            val mapper: JsonpMapper = jsonpMapper()
            val output = StringWriter()
            val generator: JsonGenerator = mapper.jsonProvider().createGenerator(output)

            mapper.serialize(request, generator)
            generator.close()

            log.info("OpenSearch request: method=${endpoint.method(request)}, endpoint=${endpoint.requestUrl(request)}, body=\n$output")
        } catch (e: Exception) {
            log.warn("Failed to serialize OpenSearch request: ${e.message}")
        }

        return delegate.performRequest(request, endpoint, options)
    }

    override fun <RequestT, ResponseT, ErrorT> performRequestAsync(
        request: RequestT,
        endpoint: Endpoint<RequestT, ResponseT, ErrorT>,
        options: TransportOptions?
    ): CompletableFuture<ResponseT> {
        return delegate.performRequestAsync(request, endpoint, options)
    }
}