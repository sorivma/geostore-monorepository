package sorivma.geosearchindexer.infrastructure.opensearch

import sorivma.geosearchindexer.domain.model.IndexedMetadata

interface OpensearchIndexService {
    fun indexDocument(doc: IndexedMetadata)
    fun updateDocument(doc: IndexedMetadata)
    fun deleteDocument(objectId: String)
}