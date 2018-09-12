package cz.fb.manaus.spring

import cz.fb.manaus.ischia.strategy.MinimizeChargeStrategy
import cz.fb.manaus.reactor.betting.BetContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("betfair")
open class BetfairValues {

    @Bean
    open fun priceBulldoze(): Double {
        return 50.0
    }

    @Bean
    open fun downgradeStrategy(): (BetContext) -> Double {
        return { minimizeChargeStrategy().getReductionRate(it) }
    }

    @Bean
    open fun minimizeChargeStrategy(): MinimizeChargeStrategy {
        return MinimizeChargeStrategy(0.01, 0.05, 0.06)
    }

}
