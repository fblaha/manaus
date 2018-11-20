package cz.fb.manaus.core.maintanance.db

import cz.fb.manaus.core.model.MarketFootprint
import java.time.Instant
import java.time.temporal.ChronoUnit

class NoSettledBetApprover : MarketDeletionApprover {

    override fun isDeletable(marketFootprint: MarketFootprint): Boolean {
        val obsolete = Instant.now().minus(24, ChronoUnit.HOURS)
        val (market, _, bets) = marketFootprint
        return bets.isEmpty() && market.event.openDate.isBefore(obsolete)
    }
}