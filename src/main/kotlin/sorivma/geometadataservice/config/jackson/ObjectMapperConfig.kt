package sorivma.geometadataservice.config.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ObjectMapperConfig {
    @Bean
    fun objectMapper(): ObjectMapper = ObjectMapper().apply {
        registerModules(JavaTimeModule(), KotlinModule.Builder().build())
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }
}
