package cz.fb.manaus.spring

import cz.fb.manaus.reactor.betting.BetContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("matchbook")
@Configuration
open class MatchbookValues {

    @Bean
    open fun priceBulldoze(): Double {
        return 100.0
    }

    @Bean
    open fun downgradeStrategy(): (BetContext) -> Double {
        return { 0.07 }
    }
}
