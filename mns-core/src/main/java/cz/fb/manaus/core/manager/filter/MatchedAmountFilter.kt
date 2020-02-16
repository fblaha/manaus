package cz.fb.manaus.core.manager.filter

import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.model.amountEq
import cz.fb.manaus.core.provider.ProviderTag
import org.springframework.stereotype.Component

@Component
class MatchedAmountFilter : FreshMarketFilter {

    override val tags get() = setOf(ProviderTag.MatchedAmount)

    override fun accept(market: Market): Boolean {
        val matchedAmount = market.matchedAmount
        return !(matchedAmount amountEq 0.0)
    }
}