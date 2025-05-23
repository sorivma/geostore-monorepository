package sorivma.geometadataservice.application.publisher.textGenerator

import sorivma.geometadataservice.domain.model.GeoMetadata

interface AnyTextGenerator {
    fun fromMetadata(meta: GeoMetadata): String
}