package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.category.categorizer.RealizedBetCategorizer
import cz.fb.manaus.core.model.RealizedBet
import org.springframework.stereotype.Component

@Component
class SelectionOrderCategorizer : RealizedBetCategorizer {

    override fun getCategories(realizedBet: RealizedBet): Set<String> {
        val market = realizedBet.market
        val runner = market.runners.first { it.selectionId == realizedBet.settledBet.selectionId }
        return setOf("selectionOrder_" + runner.sortPriority)
    }
}
