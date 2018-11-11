package cz.fb.manaus.core.category.categorizer

import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.model.RealizedBet

interface RealizedBetCategorizer : SimulationAware {

    val isMarketSnapshotRequired: Boolean
        get() = false

    fun getCategories(realizedBet: RealizedBet, coverage: BetCoverage): Set<String>

}
