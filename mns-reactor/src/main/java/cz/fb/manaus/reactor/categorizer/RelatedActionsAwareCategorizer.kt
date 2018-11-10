package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.repository.domain.BetAction
import cz.fb.manaus.core.repository.domain.Market

interface RelatedActionsAwareCategorizer {

    fun getCategories(actions: List<BetAction>, market: Market): Set<String>

}
