package sorivma.geoorchestratorservice.config.security.decoder

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtException
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.*

@Configuration
@Profile("jwt-mock")
@Primary
class LocalJwtDecoderConfig(
    private val objectMapper: ObjectMapper
) {
    @Bean
    fun jwtDecoder(): JwtDecoder {
        return JwtDecoder { token ->
            try {
                val parts = token.split(".")
                if (parts.size != 3) throw JwtException("Invalid JWT token")

                val headerJson = String(Base64.getUrlDecoder().decode(parts[0]), StandardCharsets.UTF_8)
                val claimsJson = String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8)

                val headers = objectMapper.readValue(headerJson, Map::class.java) as Map<String, *>
                val claims = objectMapper.readValue(claimsJson, Map::class.java) as Map<String, *>

                Jwt(
                    token,
                    Instant.now(),
                    Instant.now().plusSeconds(3600),
                    headers,
                    claims
                )
            } catch (ex: Exception) {
                throw JwtException("Failed to decode JWT in local profile: ${ex.message}", ex)
            }
        }
    }
}