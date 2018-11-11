package cz.fb.manaus.core.manager.filter

import cz.fb.manaus.core.model.Market

interface MarketFilter {

    val isStrict: Boolean
        get() = false

    fun accept(market: Market, categoryBlacklist: Set<String>): Boolean
}
