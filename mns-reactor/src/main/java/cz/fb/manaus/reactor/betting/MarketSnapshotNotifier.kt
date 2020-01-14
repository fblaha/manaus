package cz.fb.manaus.reactor.betting

import cz.fb.manaus.core.manager.MarketFilterService
import cz.fb.manaus.core.model.*
import cz.fb.manaus.reactor.betting.action.BetCommandHandler
import cz.fb.manaus.reactor.betting.action.BetUtils
import cz.fb.manaus.reactor.betting.listener.MarketSnapshotEvent
import cz.fb.manaus.reactor.betting.listener.MarketSnapshotListener
import io.micrometer.core.instrument.Metrics
import org.springframework.core.annotation.AnnotationAwareOrderComparator
import java.time.Instant
import java.util.logging.Logger

class MarketSnapshotNotifier(
        snapshotListeners: List<MarketSnapshotListener>,
        private val filterService: MarketFilterService,
        private val handlers: List<BetCommandHandler>
) {

    private val sortedSnapshotListeners: List<MarketSnapshotListener> =
            snapshotListeners.sortedWith(AnnotationAwareOrderComparator.INSTANCE)

    private val log = Logger.getLogger(MarketSnapshotNotifier::class.simpleName)

    fun notify(snapshot: MarketSnapshot, myBets: Set<String>, account: Account): CollectedBets {

        if (filterService.accept(snapshot.market, myBets.isNotEmpty(), account.provider::matches)) {
            validateOpenDate(snapshot.market)

            val unknownBets = BetUtils.getUnknownBets(snapshot.currentBets, myBets)
            unknownBets.forEach { log.warning { "unknown bet '$it'" } }
            if (unknownBets.isEmpty()) {
                val bets = sortedSnapshotListeners
                        .flatMap { it.onMarketSnapshot(MarketSnapshotEvent(snapshot, account)) }

                val collectedBets = toCollectedBets(callHandlers(bets))
                collectedBets.updateMetrics()
                return collectedBets
            }
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
        validateCommands(result)
        return result
    }

    private fun validateCommands(commands: List<BetCommand>) {
        commands.filter { !it.isCancel }.forEach { check(it.bet.actionId > 0) }
    }
}

const val betCommandMetricName = "mns_bet_command_total"

fun CollectedBets.updateMetrics() {
    Metrics.counter(betCommandMetricName, "type", "cancel").increment(cancel.size.toDouble())
    updateCounter(Side.BACK, "place", place)
    updateCounter(Side.LAY, "place", place)
    updateCounter(Side.BACK, "update", update)
    updateCounter(Side.LAY, "update", update)
}

private fun updateCounter(side: Side, type: String, bets: List<Bet>) {
    val count = bets.count { it.requestedPrice.side == side }
    Metrics.counter(betCommandMetricName,
            "type", type,
            "side", side.name.toLowerCase()
    ).increment(count.toDouble())
}