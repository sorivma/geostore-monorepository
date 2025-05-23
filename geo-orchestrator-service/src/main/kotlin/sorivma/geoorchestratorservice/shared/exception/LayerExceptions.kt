package sorivma.geoorchestratorservice.shared.exception

import sorivma.geoorchestratorservice.domain.model.layer.LayerType
import java.util.*

class LayerNotFoundException(layerId: UUID) : RuntimeException("Layer $layerId does not exist")
class ModifyNotVectorLayerException(layerId: UUID) : RuntimeException("Layer $layerId could not be modified")
class ObjectNotFoundInLayerException(objectId: UUID, layerId: UUID) : RuntimeException("Object $objectId does not exist in $layerId")
class VectorLayerDataNotFoundException(layerId: UUID) : RuntimeException("Layer $layerId is not initialized or is not vector")
class WrongLayerTypeException(layerId: UUID, expected: LayerType, actual: LayerType) : RuntimeException("Wrong layer type for $layerId, expected = $expected, actual = $actual")