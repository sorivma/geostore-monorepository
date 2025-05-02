package sorivma.geometadataservice

import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableRabbit
class GeoMetadataServiceApplication

fun main(args: Array<String>) {
    runApplication<GeoMetadataServiceApplication>(*args)
}
