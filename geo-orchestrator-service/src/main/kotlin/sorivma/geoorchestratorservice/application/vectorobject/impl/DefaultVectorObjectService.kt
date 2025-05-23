package sorivma.geoorchestratorservice.application.vectorobject.impl

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sorivma.geoorchestratorservice.application.layer.VectorLayerDataService
import sorivma.geoorchestratorservice.application.layer.dto.VectorLayerDataDto
import sorivma.geoorchestratorservice.application.project.ProjectAccessService
import sorivma.geoorchestratorservice.application.vectorobject.VectorObjectService
import sorivma.geoorchestratorservice.application.vectorobject.dto.CreateVectorObjectRequest
import sorivma.geoorchestratorservice.domain.model.layer.LayerType
import sorivma.geoorchestratorservice.domain.model.layer.VectorLayerData
import sorivma.geoorchestratorservice.infrastructure.messaging.publishing.FeatureCommandPublisher
import sorivma.geoorchestratorservice.infrastructure.messaging.publishing.dto.CreateGeometryRequest
import sorivma.geoorchestratorservice.infrastructure.messaging.publishing.dto.MetadataMessageDto
import sorivma.geoorchestratorservice.infrastructure.messaging.publishing.dto.UpdateGeometryRequest
import sorivma.geoorchestratorservice.infrastructure.persistence.layer.repository.LayerRepository
import sorivma.geoorchestratorservice.shared.exception.LayerNotFoundException
import sorivma.geoorchestratorservice.shared.exception.ModifyNotVectorLayerException
import sorivma.geoorchestratorservice.shared.exception.ObjectNotFoundInLayerException
import java.time.Instant
import java.util.*

@Service
class DefaultVectorObjectService(
    private val layerRepository: LayerRepository,
    private val projectAccess: ProjectAccessService,
    private val vectorLayerDataService: VectorLayerDataService,
    private val featurePublisher: FeatureCommandPublisher,
    private val objectMapper: ObjectMapper,
): VectorObjectService {

    private val logger = LoggerFactory.getLogger(DefaultVectorObjectService::class.java)

    @Transactional
    override fun create(layerId: UUID, request: CreateVectorObjectRequest): UUID {
        val layer = layerRepository.findById(layerId)
            ?: throw LayerNotFoundException(layerId)

        if (layer.type != LayerType.VECTOR)
            throw ModifyNotVectorLayerException(layerId)

        projectAccess.assertCanEdit(layer.projectId)

        val objectId = UUID.randomUUID()

        val updatedData = if (vectorLayerDataService.existsByLayerId(layerId)) {
            val currentData = vectorLayerDataService.getByLayerId(layerId)
            currentData.copy(objectIds = currentData.objectIds + objectId)
        } else {
            val style = VectorLayerData.VectorStyle()

            VectorLayerDataDto(
                objectIds = listOf(objectId),
                fillColor = style.fillColor,
                strokeColor = style.strokeColor,
                strokeWidth = style.strokeWidth
            )
        }


        vectorLayerDataService.save(layerId, updatedData)

        val geometryEvent = CreateGeometryRequest(
            objectId = objectId,
            geometry = serializeGeometry(request.geometry),
            format = request.format,
            sourceSrid = request.sourceSrid
        )
        featurePublisher.publishGeometryCreated(geometryEvent)

        val metadataEvent = MetadataMessageDto(
            objectId = objectId,
            dcType = "Dataset",
            createdAt = Instant.now(),
            temporalExtent = request.temporalExtent,
            source = "user",
            region = request.region,
            topicCategory = request.topicCategory,
            properties = request.properties
        )
        featurePublisher.publishMetadataCreated(metadataEvent)

        logger.info("Created vector object {} in layer {}", objectId, layerId)

        return objectId
    }

    override fun update(layerId: UUID, objectId: UUID, request: CreateVectorObjectRequest) {
        val layer = layerRepository.findById(layerId)
            ?: throw LayerNotFoundException(layerId)

        if (layer.type != LayerType.VECTOR)
            throw ModifyNotVectorLayerException(layerId)

        projectAccess.assertCanEdit(layer.projectId)

        val data = vectorLayerDataService.getByLayerId(layerId)

        if (!data.objectIds.contains(objectId)) {
            throw ObjectNotFoundInLayerException(layerId, objectId)
        }

        featurePublisher.publishGeometryUpdated(
            UpdateGeometryRequest(
                objectId = objectId,
                newGeometry = serializeGeometry(request.geometry),
                format = request.format,
                sourceSrid = request.sourceSrid
            )
        )

        featurePublisher.publishMetadataUpdated(
            MetadataMessageDto(
                objectId = objectId,
                dcType = "Dataset",
                createdAt = Instant.now(),
                temporalExtent = request.temporalExtent,
                source = "user",
                region = request.region,
                topicCategory = request.topicCategory,
                properties = request.properties
            )
        )

        logger.info("Updated vector object {} in layer {}", objectId, layerId)
    }

    override fun delete(layerId: UUID, objectId: UUID) {
        vectorLayerDataService.delete(objectId, layerId)
    }

    private fun serializeGeometry(geometry: Any): String {
        return when (geometry) {
            is String -> geometry
            else -> objectMapper.writeValueAsString(geometry)
        }
    }
}