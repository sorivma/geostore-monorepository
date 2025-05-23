package sorivma.geoorchestratorservice.config.rabbit

import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitConfig {
    @Bean
    fun rabbitTemplate(connectionFactory: ConnectionFactory): RabbitTemplate =
        RabbitTemplate(connectionFactory).apply {
            messageConverter = Jackson2JsonMessageConverter()
        }
}
