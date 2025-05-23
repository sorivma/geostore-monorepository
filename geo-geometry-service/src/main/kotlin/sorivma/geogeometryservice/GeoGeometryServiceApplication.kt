package sorivma.geogeometryservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy

@SpringBootApplication
@EnableAspectJAutoProxy
class GeoGeometryServiceApplication

fun main(args: Array<String>) {
    runApplication<GeoGeometryServiceApplication>(*args)
}
