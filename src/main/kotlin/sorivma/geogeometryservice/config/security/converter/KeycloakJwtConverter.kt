package sorivma.geogeometryservice.config.security.converter

import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component

@Component
class KeycloakJwtConverter : Converter<Jwt, AbstractAuthenticationToken> {
    override fun convert(jwt: Jwt): AbstractAuthenticationToken {
        val resourceAccess = jwt.claims["resource_access"] as? Map<*, *>
        val clientAccess = (resourceAccess?.get("geo-service") as? Map<*, *>)?.get("roles") as? List<*> ?: emptyList<Any>()

        val authorities = clientAccess.map {
            SimpleGrantedAuthority(it.toString().uppercase())
        }

        return JwtAuthenticationToken(jwt, authorities)
    }
}