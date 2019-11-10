package cz.fb.manaus.reactor.betting.listener

import cz.fb.manaus.core.model.Account
import cz.fb.manaus.core.model.MarketSnapshot
import cz.fb.manaus.reactor.betting.BetCommand

data class MarketSnapshotEvent(
        val snapshot: MarketSnapshot,
        val account: Account
)

interface MarketSnapshotListener {

    fun onMarketSnapshot(marketSnapshotEvent: MarketSnapshotEvent): List<BetCommand>

}
