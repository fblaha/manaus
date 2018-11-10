package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.repository.domain.SettledBet
import org.springframework.stereotype.Component

@Component
class NotMatchedCountCategorizer : AbstractCountCategorizer("notMatchedCount_", 4) {

    override fun getCount(bet: SettledBet): Int {
        val marketPrices = bet.betAction.marketPrices
        return marketPrices.runnerPrices
                .filter { rp -> rp.lastMatchedPrice == null }.count()
    }
}
