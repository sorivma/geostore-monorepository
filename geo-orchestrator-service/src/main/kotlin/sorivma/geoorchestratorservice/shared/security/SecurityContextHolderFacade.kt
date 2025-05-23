package sorivma.geoorchestratorservice.shared.security

import java.util.*

interface SecurityContextHolderFacade {
    fun currentUserId(): UUID
}