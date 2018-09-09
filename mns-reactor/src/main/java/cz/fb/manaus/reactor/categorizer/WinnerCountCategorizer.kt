package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.model.SettledBet
import org.springframework.stereotype.Component

@Component
class WinnerCountCategorizer : AbstractCountCategorizer("winnerCount_", 6) {

    override fun getCount(bet: SettledBet): Int {
        return bet.betAction.marketPrices.winnerCount
    }
}
