package cz.fb.manaus.reactor.betting.listener

import cz.fb.manaus.core.model.MarketSnapshotEvent
import cz.fb.manaus.reactor.betting.BetCommand

interface MarketSnapshotListener {

    fun onMarketSnapshot(marketSnapshotEvent: MarketSnapshotEvent): List<BetCommand>

}
