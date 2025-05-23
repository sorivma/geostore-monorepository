package sorivma.geogeometryservice.infrastructure.db.formatter.exception

class UnsupportedFormatException(format: String) : RuntimeException("Format $format is not supported")