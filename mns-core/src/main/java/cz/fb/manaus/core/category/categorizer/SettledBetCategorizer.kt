package cz.fb.manaus.core.category.categorizer

import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.model.SettledBet

interface SettledBetCategorizer : SimulationAware {

    val isMarketSnapshotRequired: Boolean
        get() = false

    fun getCategories(settledBet: SettledBet, coverage: BetCoverage): Set<String>

}
