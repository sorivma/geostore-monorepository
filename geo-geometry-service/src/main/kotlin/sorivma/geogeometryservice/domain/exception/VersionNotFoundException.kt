package sorivma.geogeometryservice.domain.exception

class VersionNotFoundException(objectId: String, version: Int) :
    RuntimeException("Version $version not found for objectId $objectId")
