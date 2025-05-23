package sorivma.geosearchindexer.infrastructure.config.rmq

import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Declarables
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitConfig {
    @Bean
    fun exchange(): TopicExchange = TopicExchange("geo.exchange")

    @Bean
    fun messageConverter(): MessageConverter = Jackson2JsonMessageConverter()

    @Bean
    fun createdQueue(): Queue = Queue("metadata.index.created.queue")

    @Bean
    fun updatedQueue(): Queue = Queue("metadata.index.updated.queue")

    @Bean
    fun deletedQueue(): Queue = Queue("metadata.index.deleted.queue")

    @Bean
    fun bindings(): Declarables = Declarables(
        BindingBuilder.bind(createdQueue()).to(exchange()).with("metadata.index.created"),
        BindingBuilder.bind(updatedQueue()).to(exchange()).with("metadata.index.updated"),
        BindingBuilder.bind(deletedQueue()).to(exchange()).with("metadata.index.deleted")
    )
}