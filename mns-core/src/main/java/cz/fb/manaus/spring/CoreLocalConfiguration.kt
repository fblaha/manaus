package cz.fb.manaus.spring

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import cz.fb.manaus.core.maintanance.db.OldMarketApprover
import cz.fb.manaus.spring.conf.MarketConf
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder


@Configuration
@ComponentScan(value = ["cz.fb.manaus.core"])
@EnableConfigurationProperties(MarketConf::class)
open class CoreLocalConfiguration {

    @Bean
    open fun objectMapperBuilder(): Jackson2ObjectMapperBuilder {
        val builder = Jackson2ObjectMapperBuilder()
        builder.modules(KotlinModule(), Jdk8Module(), JavaTimeModule())
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        builder.indentOutput(true)
        return builder
    }

    @Bean
    open fun objectMapper(): ObjectMapper {
        return objectMapperBuilder().build()
    }

    @Bean
    open fun oldMarketApprover(marketConf: MarketConf): OldMarketApprover {
        return OldMarketApprover(marketConf.history)
    }
}
