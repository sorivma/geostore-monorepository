package sorivma.geogeometryservice.application.repository

import sorivma.geogeometryservice.application.model.FormattedGeometry
import java.util.UUID

interface FormattedGeometryRepository {
    fun findFormatted(objectId: UUID, version: Int, format: String, srid: Int? = null): FormattedGeometry?
}