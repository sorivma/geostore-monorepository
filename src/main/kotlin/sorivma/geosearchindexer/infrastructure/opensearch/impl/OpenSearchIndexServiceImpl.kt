package sorivma.geosearchindexer.infrastructure.opensearch.impl

import org.opensearch.client.opensearch.OpenSearchClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import sorivma.geosearchindexer.domain.model.IndexedMetadata
import sorivma.geosearchindexer.infrastructure.config.opensearch.properties.OpenSearchProperties
import sorivma.geosearchindexer.infrastructure.opensearch.OpensearchIndexService

@Service
class OpenSearchIndexServiceImpl(
    private val client: OpenSearchClient,
    private val props: OpenSearchProperties
): OpensearchIndexService {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun indexDocument(doc: IndexedMetadata) {
        client.index {
            it.index(props.index)
                .id(doc.objectId.toString())
                .document(doc)
        }
        log.info("Indexed document: ${doc.objectId}")
    }

    override fun updateDocument(doc: IndexedMetadata) {
        indexDocument(doc)
        log.info("Updated document: ${doc.objectId}")
    }

    override fun deleteDocument(objectId: String) {
        client.delete {
            it.index(props.index).id(objectId)
        }
        log.info("Deleted document: $objectId")
    }
}