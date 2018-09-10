package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.MarketCategories
import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer
import cz.fb.manaus.core.model.SettledBet
import org.springframework.stereotype.Component

@Component
class AllCategorizer : SettledBetCategorizer {

    override fun getCategories(settledBet: SettledBet, coverage: BetCoverage): Set<String> {
        return CATEGORIES
    }

    companion object {
        val CATEGORIES = setOf(MarketCategories.ALL)
    }
}
