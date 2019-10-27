package cz.fb.manaus.reactor.betting.listener

import cz.fb.manaus.core.model.Account
import cz.fb.manaus.core.model.MarketSnapshot
import cz.fb.manaus.reactor.betting.BetCollector

data class MarketSnapshotEvent(
        val snapshot: MarketSnapshot,
        val account: Account,
        val collector: BetCollector
)

interface MarketSnapshotListener {

    fun onMarketSnapshot(event: MarketSnapshotEvent)

}
