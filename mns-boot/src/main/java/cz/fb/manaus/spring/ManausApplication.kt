package cz.fb.manaus.spring

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableAutoConfiguration
open class ManausApplication

fun main(args: Array<String>) {
    runApplication<ManausApplication>(*args) {
        setAdditionalProfiles(*ManausProfiles.PRODUCTION_REQUIRED)
    }
}