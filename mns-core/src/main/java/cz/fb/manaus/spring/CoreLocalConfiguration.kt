package cz.fb.manaus.spring

import com.codahale.metrics.MetricRegistry
import cz.fb.manaus.core.provider.ExchangeProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

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
}
