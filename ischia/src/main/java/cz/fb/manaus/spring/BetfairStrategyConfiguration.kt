package cz.fb.manaus.spring

import cz.fb.manaus.ischia.strategy.MinimizeChargeStrategy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("betfair")
open class BetfairStrategyConfiguration {

    @Bean
    open fun minimizeChargeStrategy(): MinimizeChargeStrategy {
        return MinimizeChargeStrategy(0.01, 0.05, 0.06)
    }
}
