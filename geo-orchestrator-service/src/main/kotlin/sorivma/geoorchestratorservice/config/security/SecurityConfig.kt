package sorivma.geoorchestratorservice.config.security

import org.apache.catalina.webresources.TomcatURLStreamHandlerFactory.disable
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
            .cors {  }
            .csrf { disable() }
            .authorizeHttpRequests {
                it
                    .requestMatchers("/actuator/prometheus").permitAll()
                    .requestMatchers("/projects/**").hasAnyRole("ADMIN", "VIEWER", "EDITOR")
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