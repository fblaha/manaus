package cz.fb.manaus.core.manager.filter

import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.model.MarketSnapshotEvent

interface FreshMarketValidator : MarketSnapshotEventValidator {

    override fun accept(event: MarketSnapshotEvent): Boolean {
        if (event.snapshot.currentBets.isNotEmpty()) {
            return true
        }
        return accept(event.snapshot.market)
    }

    fun accept(market: Market): Boolean
}
