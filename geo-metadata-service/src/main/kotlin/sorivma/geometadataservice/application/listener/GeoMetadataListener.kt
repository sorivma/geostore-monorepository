package sorivma.geometadataservice.application.listener

import sorivma.geometadataservice.application.listener.dto.MetadataMessageDto

interface GeoMetadataListener {
    fun handleCreate(dto: MetadataMessageDto)
    fun handleUpdate(dto: MetadataMessageDto)
    fun handleDelete(objectId: String)
}