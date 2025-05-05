package sorivma.geoorchestratorservice.shared.exception

class MalformedGeoJsonException(msg: String) : RuntimeException(msg)
class FileTooBigException(size: Long, limit: Long) : RuntimeException("File of size $size exceeds limit of $size")