package sorivma.geoorchestratorservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class GeoOrchestratorServiceApplication

fun main(args: Array<String>) {
    runApplication<GeoOrchestratorServiceApplication>(*args)
}
