package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer
import cz.fb.manaus.core.model.SettledBet

abstract class AbstractCountCategorizer protected constructor(private val prefix: String, private val maxCount: Int) : SettledBetCategorizer {

    override val isMarketSnapshotRequired: Boolean = true

    override fun getCategories(settledBet: SettledBet, coverage: BetCoverage): Set<String> {
        val count = getCount(settledBet)
        return setOf(prefix + toCategory(count))
    }

    protected abstract fun getCount(bet: SettledBet): Int

    private fun toCategory(count: Int): String {
        return if (count >= maxCount) {
            maxCount.toString() + "+"
        } else {
            Integer.toString(count)
        }
    }
}
