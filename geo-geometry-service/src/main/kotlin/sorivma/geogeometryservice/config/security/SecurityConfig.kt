package sorivma.geogeometryservice.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig(
    private val jwtAuthenticationConverter: Converter<Jwt, out AbstractAuthenticationToken>,
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .authorizeHttpRequests {
                it
                    .requestMatchers("/actuator/prometheus").permitAll()
                    .requestMatchers(HttpMethod.GET, "/geometries/**").hasAnyRole("EDITOR", "VIEWER", "ADMIN")
                    .requestMatchers(HttpMethod.POST, "/geometries/**").hasAnyRole("EDITOR", "ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/geometries/**").hasAnyRole("EDITOR", "ADMIN")
                    .requestMatchers(HttpMethod.PATCH, "/geometries/**").hasAnyRole("EDITOR", "ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/geometries/**").hasAnyRole("EDITOR", "ADMIN")
                    .requestMatchers("/spatial/**").hasAnyRole("EDITOR", "ADMIN", "VIEWER")
                    .anyRequest().authenticated()
            }
            .oauth2ResourceServer { resourceServer ->
                resourceServer.jwt { jwtConfigurer ->
                    jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter)
                }
            }
            .build()
    }
}