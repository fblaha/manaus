package cz.fb.manaus.core.manager.filter

import cz.fb.manaus.core.repository.domain.Market

interface MarketFilter {

    val isStrict: Boolean
        get() = false

    fun accept(market: Market, categoryBlacklist: Set<String>): Boolean
}
