package cz.fb.manaus.core.maintanance.db

import cz.fb.manaus.core.model.MarketFootprint
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit

@Component
class NoBetActionApprover : MarketDeletionApprover {

    override val timeRange: TimeRange?
        get() = TimeRange(
                Instant.now().minus(7, ChronoUnit.DAYS),
                Instant.now().minus(6, ChronoUnit.HOURS)
        )

    override fun isDeletable(marketFootprint: MarketFootprint): Boolean {
        val obsolete = Instant.now().minus(6, ChronoUnit.HOURS)
        val (market, betActions, _) = marketFootprint
        return betActions.isEmpty() && market.event.openDate.isBefore(obsolete)
    }
}