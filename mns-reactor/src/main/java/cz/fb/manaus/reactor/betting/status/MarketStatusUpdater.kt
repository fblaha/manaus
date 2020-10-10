package cz.fb.manaus.reactor.betting.status

import cz.fb.manaus.core.model.MarketSnapshotEvent
import cz.fb.manaus.core.model.MarketStatus
import cz.fb.manaus.core.repository.Repository
import cz.fb.manaus.reactor.betting.BetCommand
import cz.fb.manaus.reactor.betting.listener.MarketSnapshotListener
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Instant

@Component
@Profile(ManausProfiles.DB)
class MarketStatusUpdater(
        private val repository: Repository<MarketStatus>
) : MarketSnapshotListener {

    override fun onMarketSnapshot(marketSnapshotEvent: MarketSnapshotEvent): List<BetCommand> {
        val snapshot = marketSnapshotEvent.snapshot
        val market = snapshot.market
        val openDate = market.event.openDate
        // TODO maybe not best place for distinct
        val bets = snapshot.currentBets.distinctBy { it.betId }.sortedBy { it.requestedPrice.side }
        val state = MarketStatus(
                id = market.id,
                openDate = openDate,
                lastEvent = Instant.now(),
                bets = bets
        )
        repository.saveOrUpdate(state)
        return emptyList()
    }
}