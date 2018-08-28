package cz.fb.manaus.spring

import cz.fb.manaus.reactor.betting.BetContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.util.function.Function

@Profile("matchbook")
@Configuration
open class MatchbookValues {

    @Bean
    open fun priceBulldoze(): Double {
        return 100.0
    }

    @Bean
    open fun downgradeStrategy(): Function<BetContext, Double> {
        return Function { this.strategy(it) }
    }

    private fun strategy(context: BetContext): Double {
        val type = context.marketPrices.market.type
        return RATES.getOrDefault(type, 0.08)
    }

    companion object {
        private val RATES = mapOf("moneyline" to 0.1)
    }
}