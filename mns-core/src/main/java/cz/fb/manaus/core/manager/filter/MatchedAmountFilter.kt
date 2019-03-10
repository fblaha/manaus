package cz.fb.manaus.core.manager.filter

import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.model.amountEq
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("betfair")
class MatchedAmountFilter : MarketFilter {

    override fun accept(market: Market): Boolean {
        val matchedAmount = market.matchedAmount
        return !(matchedAmount amountEq 0.0)
    }
}