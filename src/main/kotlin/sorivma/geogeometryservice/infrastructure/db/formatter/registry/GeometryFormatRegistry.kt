package sorivma.geogeometryservice.infrastructure.db.formatter.registry

import sorivma.geogeometryservice.infrastructure.db.formatter.formatter.GeometrySqlFormatter

interface GeometryFormatRegistry {
    fun get(format: String): GeometrySqlFormatter
    fun supportedFormats(): Set<String>
}