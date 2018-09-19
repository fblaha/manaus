package cz.fb.manaus.reactor.betting.listener

import cz.fb.manaus.core.model.AccountMoney
import cz.fb.manaus.core.model.MarketSnapshot
import cz.fb.manaus.reactor.betting.BetCollector
import java.util.*

interface MarketSnapshotListener {

    fun onMarketSnapshot(snapshot: MarketSnapshot,
                         betCollector: BetCollector,
                         accountMoney: Optional<AccountMoney>,
                         categoryBlacklist: Set<String>)

}
