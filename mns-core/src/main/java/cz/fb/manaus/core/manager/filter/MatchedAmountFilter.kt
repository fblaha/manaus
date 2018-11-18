package cz.fb.manaus.core.manager.filter

import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.model.Price
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("betfair")
class MatchedAmountFilter : MarketFilter {

    override fun accept(market: Market, categoryBlacklist: Set<String>): Boolean {
        val matchedAmount = market.matchedAmount
        return !Price.amountEq(matchedAmount, 0.0)
    }
}