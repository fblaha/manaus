package cz.fb.manaus.reactor.betting.listener

import cz.fb.manaus.core.model.Account
import cz.fb.manaus.core.model.MarketSnapshot
import cz.fb.manaus.reactor.betting.BetCollector

interface MarketSnapshotListener {

    fun onMarketSnapshot(snapshot: MarketSnapshot,
                         betCollector: BetCollector,
                         account: Account)

}
