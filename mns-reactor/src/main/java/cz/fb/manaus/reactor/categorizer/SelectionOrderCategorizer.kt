package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer
import cz.fb.manaus.core.model.SettledBet
import org.springframework.stereotype.Component

@Component
class SelectionOrderCategorizer : SettledBetCategorizer {

    override fun getCategories(settledBet: SettledBet, coverage: BetCoverage): Set<String> {
        val market = settledBet.betAction.market
        val runner = market.runners
                .find { r -> r.selectionId == settledBet.selectionId }
        return setOf("selectionOrder_" + runner!!.sortPriority)
    }
}
