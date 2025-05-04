package sorivma.geosearchindexer.infrastructure.config.aspect

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Aspect
@Component
@Profile("rest-audit-logs")
class RestAuditAspect {
    private val logger = LoggerFactory.getLogger("RestAudit")

    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    fun logUserCall(joinPoint: ProceedingJoinPoint): Any? {
        val auth = SecurityContextHolder.getContext().authentication
        val username = auth?.name ?: "anonymous"
        val method = joinPoint.signature.toShortString()

        logger.info("User [$username] is calling [$method]")

        return joinPoint.proceed()
    }
}