package sorivma.geoorchestratorservice.application.vectorobject.impl

import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import sorivma.geoorchestratorservice.application.project.ProjectAccessService
import sorivma.geoorchestratorservice.application.vectorobject.VectorObjectSearchService
import sorivma.geoorchestratorservice.application.vectorobject.dto.SearchResultDto
import sorivma.geoorchestratorservice.domain.model.layer.LayerType
import sorivma.geoorchestratorservice.infrastructure.persistence.layer.repository.LayerRepository
import sorivma.geoorchestratorservice.infrastructure.persistence.layer.repository.VectorLayerDataRepository
import sorivma.geoorchestratorservice.infrastructure.query.GeoSearchClient
import sorivma.geoorchestratorservice.infrastructure.query.GeometryQueryClient
import sorivma.geoorchestratorservice.infrastructure.query.dto.*
import sorivma.geoorchestratorservice.shared.exception.LayerNotFoundException
import java.util.*

@Service
class DefaultVectorObjectSearchService(
    private val layerDataRepository: VectorLayerDataRepository,
    private val layerRepository: LayerRepository,
    private val geometryQueryClient: GeometryQueryClient,
    private val accessService: ProjectAccessService,
    private val geoSearchClient: GeoSearchClient
) : VectorObjectSearchService {
    private val log = LoggerFactory.getLogger(DefaultVectorObjectSearchService::class.java)

    override fun searchInLayer(
        layerId: UUID,
        query: String,
        filters: SearchFilters?,
        temporal: TemporalFilter?,
        pagination: PageRequest,
        sort: SortRequest
    ): List<SearchResultDto> {
        val objectIds = layerDataRepository.findByLayerId(layerId)?.objectIds ?: emptyList()
        val projectId = layerRepository.findById(layerId)?.projectId ?: throw LayerNotFoundException(layerId)

        accessService.assertCanView(projectId)

        log.info("Search in layer: $layerId (project: $projectId), objectIds: ${objectIds.size}, query: \"$query\"")


        val request = SearchQueryRequest(
            objectIds = objectIds,
            query = query,
            filters = filters,
            temporal = temporal,
            pagination = pagination,
            sort = sort
        )

        log.info("SearchQueryRequest: $request")


        val response = geoSearchClient.search(request, bearerToken())

        log.info("Search result: ${response.total} hits (page ${response.page}, size ${response.size})")


        return response.items.map {
            SearchResultDto(
                layerId = layerId,
                projectId = projectId,
                geoJsonGeometry = geometryQueryClient.getByObjectIds(
                    listOf(it.objectId),
                    "geojson",
                    4326,
                    bearerToken()
                ).first().geometry,
                anyText = it.anyText
            )
        }
    }


    override fun searchInProject(
        projectId: UUID,
        query: String,
        filters: SearchFilters?,
        temporal: TemporalFilter?,
        pagination: PageRequest,
        sort: SortRequest
    ): List<SearchResultDto> {
        accessService.assertCanView(projectId)

        val layers = layerRepository.findByProject(projectId)
            .filter { it.type != LayerType.RASTER }

        val objectIdToLayerId: Map<UUID, UUID> = layers.flatMap { layer ->
            val objectIds = layerDataRepository.findByLayerId(layer.id)?.objectIds ?: emptyList()
            objectIds.map { it to layer.id }
        }.toMap()

        val objectIds = objectIdToLayerId.keys.toList()

        log.info("Search in project: $projectId, layers: ${layers.size}, objectIds: ${objectIds.size}, query: \"$query\"")

        val request = SearchQueryRequest(
            objectIds = objectIds,
            query = query,
            filters = filters,
            temporal = temporal,
            pagination = pagination,
            sort = sort
        )

        log.info("SearchQueryRequest: $request")

        val response = geoSearchClient.search(request, bearerToken())

        log.info("Search result: ${response.total} hits (page ${response.page}, size ${response.size})")

        val geometries = geometryQueryClient.getByObjectIds(
            response.items.map { it.objectId },
            format = "geojson",
            srid = 4326,
            authHeader = bearerToken()
        ).associateBy { it.objectId }

        return response.items.map { result ->
            SearchResultDto(
                layerId = objectIdToLayerId[result.objectId]
                    ?: error("LayerId not found for objectId=${result.objectId}"),
                projectId = projectId,
                geoJsonGeometry = geometries[result.objectId]?.geometry
                    ?: error("Geometry not found for objectId=${result.objectId}"),
                anyText = result.anyText
            )
        }
    }

    private fun bearerToken(): String {
        val auth = SecurityContextHolder.getContext().authentication
        val token = (auth.credentials as? Jwt) ?: throw IllegalStateException("No token found")
        return "Bearer ${token.tokenValue}"
    }
}