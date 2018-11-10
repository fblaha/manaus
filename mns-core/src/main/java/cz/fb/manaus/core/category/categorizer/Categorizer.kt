package cz.fb.manaus.core.category.categorizer

import cz.fb.manaus.core.repository.domain.Market

// TODO Categorizer -> Classifier
interface Categorizer : SimulationAware {

    fun getCategories(market: Market): Set<String>

}
