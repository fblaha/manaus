package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.core.model.Side

abstract class AbstractPriceCountCategorizer(prefix: String, maxCount: Int, private val side: Side) : AbstractCountCategorizer(prefix, maxCount) {

    override fun getCount(bet: SettledBet): Int {
        return bet.betAction.marketPrices.getRunnerPrices(bet.selectionId)
                .getHomogeneous(side).prices.size
    }
}
