package cz.fb.manaus.reactor.betting

import cz.fb.manaus.core.manager.MarketSnapshotEventFilterService
import cz.fb.manaus.core.model.*
import cz.fb.manaus.reactor.betting.action.BetCommandHandler
import cz.fb.manaus.reactor.betting.listener.MarketSnapshotListener
import io.micrometer.core.instrument.Metrics
import org.springframework.core.annotation.AnnotationAwareOrderComparator
import java.time.Instant

class MarketSnapshotNotifier(
        snapshotListeners: List<MarketSnapshotListener>,
        private val eventFilterService: MarketSnapshotEventFilterService,
        private val handlers: List<BetCommandHandler>
) {

    private val sortedSnapshotListeners: List<MarketSnapshotListener> =
            snapshotListeners.sortedWith(AnnotationAwareOrderComparator.INSTANCE)

    fun notify(event: MarketSnapshotEvent): CollectedBets {
        val market = event.snapshot.market
        if (eventFilterService.accept(event)) {
            validateOpenDate(market)
            val bets = sortedSnapshotListeners.flatMap { it.onMarketSnapshot(event) }

            val collectedBets = toCollectedBets(callHandlers(bets))
            collectedBets.updateMetrics()
            return collectedBets
        }
        return CollectedBets(emptyList(), emptyList(), emptyList())
    }

    private fun validateOpenDate(market: Market) {
        val now = Instant.now()
        val openDate = market.event.openDate
        check(now.isBefore(openDate)) { "current $now open date $openDate" }
    }

    private fun callHandlers(commands: List<BetCommand>): List<BetCommand> {
        var result = commands
        for (handler in handlers) {
            result = result.map { handler.onBetCommand(it) }
        }
        return result
    }

}

fun CollectedBets.updateMetrics() {
    updateCounter(Side.BACK, "place", place)
    updateCounter(Side.LAY, "place", place)
    updateCounter(Side.BACK, "update", update)
    updateCounter(Side.LAY, "update", update)
}

private fun updateCounter(side: Side, type: String, bets: List<TrackedBet>) {
    val count = bets.count { it.remote.requestedPrice.side == side }
    Metrics.counter(
        "mns_bet_command_count",
        "type", type,
        "side", side.name.lowercase()
    ).increment(count.toDouble())
}