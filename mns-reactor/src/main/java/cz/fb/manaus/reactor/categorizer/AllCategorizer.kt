package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.MarketCategories
import cz.fb.manaus.core.category.categorizer.RealizedBetCategorizer
import cz.fb.manaus.core.model.RealizedBet
import org.springframework.stereotype.Component

@Component
object AllCategorizer : RealizedBetCategorizer {

    override fun getCategories(realizedBet: RealizedBet): Set<String> {
        return setOf(MarketCategories.ALL)
    }

}
