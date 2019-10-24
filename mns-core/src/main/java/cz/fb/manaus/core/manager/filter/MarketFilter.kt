package cz.fb.manaus.core.manager.filter

import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.provider.ProviderSelector

interface MarketFilter : ProviderSelector {

    val isStrict: Boolean
        get() = false

    fun accept(market: Market): Boolean
}
