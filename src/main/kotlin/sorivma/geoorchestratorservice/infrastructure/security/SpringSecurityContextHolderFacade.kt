package sorivma.geoorchestratorservice.infrastructure.security

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import sorivma.geoorchestratorservice.shared.security.SecurityContextHolderFacade
import java.util.*

@Component
class SpringSecurityContextHolderFacade : SecurityContextHolderFacade {
    override fun currentUserId(): UUID {
        val auth = SecurityContextHolder.getContext().authentication
        return UUID.fromString(auth.name)
    }
}