package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.categorizer.RealizedBetCategorizer
import cz.fb.manaus.core.repository.domain.RealizedBet
import cz.fb.manaus.core.repository.domain.SettledBet

abstract class AbstractCountCategorizer(private val prefix: String, private val maxCount: Int) : RealizedBetCategorizer {

    override val isMarketSnapshotRequired: Boolean = true

    override fun getCategories(realizedBet: RealizedBet, coverage: BetCoverage): Set<String> {
        val count = getCount(realizedBet)
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
