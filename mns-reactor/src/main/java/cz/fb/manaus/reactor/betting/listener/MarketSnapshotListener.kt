package cz.fb.manaus.reactor.betting.listener

import cz.fb.manaus.core.model.AccountMoney
import cz.fb.manaus.core.model.MarketSnapshot
import cz.fb.manaus.reactor.betting.BetCollector

interface MarketSnapshotListener {

    fun onMarketSnapshot(snapshot: MarketSnapshot,
                         betCollector: BetCollector,
                         accountMoney: AccountMoney?,
                         categoryBlacklist: Set<String>)

}
