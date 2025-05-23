package sorivma.geoorchestratorservice.application.project

import java.util.UUID

interface ProjectAccessService {
    fun assertCanEdit(projectId: UUID)
    fun assertCanView(projectId: UUID)
}