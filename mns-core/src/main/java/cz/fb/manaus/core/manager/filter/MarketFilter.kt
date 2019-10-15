package cz.fb.manaus.core.manager.filter

import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.provider.RequiredCapabilitiesAware

interface MarketFilter : RequiredCapabilitiesAware {

    val isStrict: Boolean
        get() = false

    fun accept(market: Market): Boolean
}
