package sorivma.geoorchestratorservice.infrastructure.messaging.publishing.dto

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import java.util.*

class UUIDDeserializer : JsonDeserializer<UUID>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): UUID {
        return UUID.fromString(p.text)
    }
}

data class CreateGeometryRequest(
    @JsonDeserialize(using = UUIDDeserializer::class)
    val objectId: UUID,
    val geometry: String,
    val format: String,
    val sourceSrid: Int
)
