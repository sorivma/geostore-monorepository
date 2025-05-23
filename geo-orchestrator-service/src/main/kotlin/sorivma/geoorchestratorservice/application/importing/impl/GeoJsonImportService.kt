package sorivma.geoorchestratorservice.application.importing.impl

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import sorivma.geoorchestratorservice.application.importing.ImportFile
import sorivma.geoorchestratorservice.application.layer.LayerService
import sorivma.geoorchestratorservice.application.layer.VectorLayerDataService
import sorivma.geoorchestratorservice.application.layer.dto.VectorLayerDataDto
import sorivma.geoorchestratorservice.application.project.ProjectAccessService
import sorivma.geoorchestratorservice.domain.model.layer.VectorLayerData
import sorivma.geoorchestratorservice.infrastructure.messaging.publishing.FeatureCommandPublisher
import sorivma.geoorchestratorservice.infrastructure.messaging.publishing.dto.CreateGeometryRequest
import sorivma.geoorchestratorservice.infrastructure.messaging.publishing.dto.MetadataMessageDto
import sorivma.geoorchestratorservice.infrastructure.messaging.publishing.dto.TemporalExtentMessageDto
import sorivma.geoorchestratorservice.shared.exception.MalformedGeoJsonException
import java.io.InputStream
import java.io.InputStreamReader
import java.time.Instant
import java.time.format.DateTimeParseException
import java.util.*

@Service
class GeoJsonImportService(
    private val objectMapper: ObjectMapper,
    private val eventPublisher: FeatureCommandPublisher,
    private val layerService: LayerService,
    private val vectorLayerDataService: VectorLayerDataService,
) : ImportFile {

    private val log = LoggerFactory.getLogger(GeoJsonImportService::class.java)

    override fun import(layerId: UUID, inputStream: InputStream) {
        layerService.getById(layerId)

        val root = objectMapper.readTree(InputStreamReader(inputStream))

        if (root["type"]?.asText() != "FeatureCollection") {
            throw MalformedGeoJsonException("Expected FeatureCollection at root of object")
        }

        val features = root["features"] ?: throw MalformedGeoJsonException("Expected features at root of object")

        val featureBuffer = mutableListOf<Pair<UUID, JsonNode>>()
        val objectIds = mutableListOf<UUID>()

        for ((i, feature) in features.withIndex()) {
            try {
                val objectId = UUID.randomUUID()

                featureBuffer += objectId to feature
                objectIds += objectId
                log.info("Buffered feature[$i] with objectId=$objectId")
            } catch (ex: Exception) {
                log.warn("Failed to process feature[$i]: ${ex.message}", ex)
            }
        }

        if (objectIds.isEmpty()) {
            log.info("No valid features found for import")
            return
        }

        val updatedData = if (vectorLayerDataService.existsByLayerId(layerId)) {
            val current = vectorLayerDataService.getByLayerId(layerId)
            current.copy(objectIds = (current.objectIds + objectIds).distinct())
        } else {
            val style = VectorLayerData.VectorStyle()
            VectorLayerDataDto(
                objectIds = objectIds,
                fillColor = style.fillColor,
                strokeColor = style.strokeColor,
                strokeWidth = style.strokeWidth
            )
        }

        vectorLayerDataService.save(layerId, updatedData)
        log.info("Successfully updated vector layer data for layerId=$layerId with ${objectIds.size} objects")

        for ((objectId, feature) in featureBuffer) {
            try {
                val geometryNode = feature["geometry"] ?: throw MalformedGeoJsonException("Expected geometry at root of feature")
                val geometryRequest = CreateGeometryRequest(
                    objectId,
                    objectMapper.writeValueAsString(geometryNode),
                    sourceSrid = DEFAULT_SRID,
                    format = FORMAT,
                )
                eventPublisher.publishGeometryCreated(geometryRequest)

                val propsNode = feature["properties"]!!

                val dcType = propsNode.get("dcType")?.asText() ?: "Dataset"
                val region = propsNode.get("region")?.asText()
                val source = propsNode.get("source")?.asText()

                val topicCategory = propsNode.get("topicCategory")?.let { node ->
                    when {
                        node.isArray -> node.mapNotNull { it.asText() }
                        node.isTextual -> listOf(node.asText())
                        else -> null
                    }
                }

                val ignoredKeys = setOf("dcType", "region", "topicCategory", "source", "startDate", "endDate")
                val properties = propsNode.fields()?.asSequence()
                    ?.filterNot { (key, _) -> key in ignoredKeys }
                    ?.associate { (key, value) -> key to objectMapper.convertValue(value, Any::class.java) }

                val metadataDto = MetadataMessageDto(
                    objectId = objectId,
                    createdAt = Instant.now(),
                    dcType = dcType,
                    region = region,
                    source = source,
                    topicCategory = topicCategory,
                    properties = properties,
                    temporalExtent = extractTemporal(propsNode),
                )
                eventPublisher.publishMetadataCreated(metadataDto)
                log.info("Published metadata and geometry for objectId=$objectId")
            } catch (ex: Exception) {
                log.warn("Failed to publish messages for objectId=$objectId: ${ex.message}", ex)
            }
        }

        log.info("Finished importing ${featureBuffer.size} features into layerId=$layerId")
    }

    private fun extractTemporal(props: JsonNode?): TemporalExtentMessageDto? {
        val startText = props?.get("startDate")?.asText()
        val endText = props?.get("endDate")?.asText()

        return try {
            if (startText != null) {
                val start = Instant.parse(startText)
                val end = endText?.let { Instant.parse(it) }
                TemporalExtentMessageDto(start, end)
            } else {
                null
            }
        } catch (e: DateTimeParseException) {
            log.warn("Invalid temporal extent: $startText - $endText", e)
            null
        }
    }

    companion object {
        const val DEFAULT_SRID = 4326
        const val FORMAT = "geojson"
    }
}