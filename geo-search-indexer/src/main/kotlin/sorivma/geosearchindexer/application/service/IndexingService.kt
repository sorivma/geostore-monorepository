package sorivma.geosearchindexer.application.service

import sorivma.geosearchindexer.shared.dto.MetadataIndexDto

interface IndexingService {
    fun index(dto: MetadataIndexDto)
    fun update(dto: MetadataIndexDto)
    fun delete(objectId: String)
}