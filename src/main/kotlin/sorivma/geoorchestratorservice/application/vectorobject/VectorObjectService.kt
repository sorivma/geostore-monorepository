package sorivma.geoorchestratorservice.application.vectorobject

import sorivma.geoorchestratorservice.application.vectorobject.dto.CreateVectorObjectRequest
import java.util.UUID

interface VectorObjectService {
    fun create(layerId: UUID, request: CreateVectorObjectRequest): UUID
    fun update(layerId: UUID, objectId: UUID, request: CreateVectorObjectRequest)
    fun delete(layerId: UUID, objectId: UUID)
}