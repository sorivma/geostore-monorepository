package sorivma.geogeometryservice.config.rmq

import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Declarables
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitConfig {
    @Bean
    fun messageConverter(): MessageConverter = Jackson2JsonMessageConverter()

    @Bean
    fun rabbitTemplate(connectionFactory: ConnectionFactory): RabbitTemplate {
        val template = RabbitTemplate(connectionFactory)
        template.messageConverter = messageConverter()
        return template
    }

    @Bean
    fun exchange() = TopicExchange("geo.exchange", true, false)

    @Bean
    fun geometryCreatedQueue() = Queue("geometry.in.created.queue", true)

    @Bean
    fun geometryUpdatedQueue() = Queue("geometry.in.updated.queue", true)

    @Bean
    fun geometryDeletedQueue() = Queue("geometry.in.deleted.queue", true)

    @Bean
    fun bindings(): Declarables = Declarables(
        BindingBuilder.bind(geometryCreatedQueue()).to(exchange()).with("geometry.created"),
        BindingBuilder.bind(geometryUpdatedQueue()).to(exchange()).with("geometry.updated"),
        BindingBuilder.bind(geometryUpdatedQueue()).to(exchange()).with("geometry.deleted")
    )
}