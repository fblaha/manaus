package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.MarketCategories
import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.categorizer.RealizedBetCategorizer
import cz.fb.manaus.core.repository.domain.RealizedBet
import org.springframework.stereotype.Component

@Component
class AllCategorizer : RealizedBetCategorizer {

    override fun getCategories(realizedBet: RealizedBet, coverage: BetCoverage): Set<String> {
        return CATEGORIES
    }

    companion object {
        val CATEGORIES = setOf(MarketCategories.ALL)
    }
}
