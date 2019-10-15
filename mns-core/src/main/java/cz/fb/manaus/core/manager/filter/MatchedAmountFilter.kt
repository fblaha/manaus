package cz.fb.manaus.core.manager.filter

import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.model.amountEq
import cz.fb.manaus.core.provider.ProviderCapability
import org.springframework.stereotype.Component

@Component
class MatchedAmountFilter : MarketFilter {

    override val requiredCapabilities: Set<ProviderCapability>
        get() = setOf(ProviderCapability.MatchedAmount)

    override fun accept(market: Market): Boolean {
        val matchedAmount = market.matchedAmount
        return !(matchedAmount amountEq 0.0)
    }
}