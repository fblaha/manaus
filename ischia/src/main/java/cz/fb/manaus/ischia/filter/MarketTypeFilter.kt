package cz.fb.manaus.ischia.filter

import com.google.common.base.Strings
import cz.fb.manaus.core.manager.filter.MarketFilter
import cz.fb.manaus.core.model.Market
import org.springframework.stereotype.Component

@Component
class MarketTypeFilter : MarketFilter {

    private val allowedTypes = setOf(
            "three_way",
            "match_odds",
            "rt_match_odds",
            "moneyline")

    override fun accept(market: Market, categoryBlacklist: Set<String>): Boolean {
        val type = Strings.nullToEmpty(market.type).toLowerCase()
        return allowedTypes.contains(type)
    }
}
