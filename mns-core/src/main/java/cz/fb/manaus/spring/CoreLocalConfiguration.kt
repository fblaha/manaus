package cz.fb.manaus.spring

import com.codahale.metrics.MetricRegistry
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import cz.fb.manaus.core.provider.ExchangeProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import java.text.SimpleDateFormat
import java.util.*


@Configuration
@ComponentScan(value = ["cz.fb.manaus.core"])
open class CoreLocalConfiguration {

    @Bean
    open fun metricRegistry(): MetricRegistry {
        return MetricRegistry()
    }

    @Bean
    @Profile("matchbook")
    open fun matchbookExchangeProvider(): ExchangeProvider {
        return ExchangeProvider("matchbook", 2.0, 1.001, 0.0075, false)
    }

    @Bean
    @Profile("betfair")
    open fun betfairExchangeProvider(): ExchangeProvider {
        return ExchangeProvider("betfair", 2.0, 1.01, 0.065, true)
    }

    @Bean
    open fun objectMapperBuilder(): Jackson2ObjectMapperBuilder {
        val builder = Jackson2ObjectMapperBuilder()
        builder.modules(KotlinModule(), Jdk8Module(), JavaTimeModule())
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        builder.indentOutput(true).dateFormat(SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
        builder.timeZone(TimeZone.getDefault())
        return builder
    }

    @Bean
    open fun objectMapper(): ObjectMapper {
        return objectMapperBuilder().build()
    }

}
