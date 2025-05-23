package sorivma.geogeometryservice.domain.exception

class VersionConflictException(objectId: String, attemptedVersion: Int) :
        RuntimeException("Version conflict for objectId $objectId with attempted version $attemptedVersion")