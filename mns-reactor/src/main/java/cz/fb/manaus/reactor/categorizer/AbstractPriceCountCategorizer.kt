package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.model.getRunnerPrices

abstract class AbstractPriceCountCategorizer(prefix: String, maxCount: Int, private val side: Side) : AbstractCountCategorizer(prefix, maxCount) {

    override fun getCount(bet: RealizedBet): Int {
        return getRunnerPrices(bet.betAction.runnerPrices, bet.settledBet.selectionId)
                .getHomogeneous(side).prices.size
    }
}
