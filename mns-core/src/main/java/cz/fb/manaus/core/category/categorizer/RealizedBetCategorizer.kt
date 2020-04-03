package cz.fb.manaus.core.category.categorizer

import cz.fb.manaus.core.model.RealizedBet

interface RealizedBetCategorizer : SimulationAware {

    fun getCategories(realizedBet: RealizedBet): Set<String>

}
