package sorivma.geosearchindexer.application.service.impl

import org.springframework.stereotype.Service
import sorivma.geosearchindexer.application.service.IndexingService
import sorivma.geosearchindexer.application.service.mapper.toIndexed
import sorivma.geosearchindexer.infrastructure.opensearch.OpensearchIndexService
import sorivma.geosearchindexer.shared.dto.MetadataIndexDto

@Service
class IndexingServiceImpl(
    private val openSearchIndexService: OpensearchIndexService
): IndexingService {
    override fun index(dto: MetadataIndexDto) {
        val doc = dto.toIndexed()
        openSearchIndexService.indexDocument(doc)
    }

    override fun update(dto: MetadataIndexDto) {
        val doc = dto.toIndexed()
        openSearchIndexService.updateDocument(doc)
    }

    override fun delete(objectId: String) {
        openSearchIndexService.deleteDocument(objectId)
    }
}