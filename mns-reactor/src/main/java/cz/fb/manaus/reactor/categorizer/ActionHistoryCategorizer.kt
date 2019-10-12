package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.model.BetAction
import cz.fb.manaus.core.model.Market

interface ActionHistoryCategorizer {

    fun getCategories(actions: List<BetAction>, market: Market): Set<String>

}
