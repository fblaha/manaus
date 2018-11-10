package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.categorizer.RealizedBetCategorizer
import cz.fb.manaus.core.repository.domain.RealizedBet
import org.springframework.stereotype.Component

@Component
class SelectionOrderCategorizer : RealizedBetCategorizer {

    override fun getCategories(realizedBet: RealizedBet, coverage: BetCoverage): Set<String> {
        val market = realizedBet.market
        val runner = market.runners
                .find { r -> r.selectionId == realizedBet.settledBet.selectionId }
        return setOf("selectionOrder_" + runner!!.sortPriority)
    }
}
