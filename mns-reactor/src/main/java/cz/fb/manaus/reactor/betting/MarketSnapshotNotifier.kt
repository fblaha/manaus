package cz.fb.manaus.reactor.betting

import cz.fb.manaus.core.manager.MarketFilterService
import cz.fb.manaus.core.model.Account
import cz.fb.manaus.core.model.CollectedBets
import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.model.MarketSnapshot
import cz.fb.manaus.reactor.betting.action.BetCommandHandler
import cz.fb.manaus.reactor.betting.action.BetUtils
import cz.fb.manaus.reactor.betting.listener.MarketSnapshotEvent
import cz.fb.manaus.reactor.betting.listener.MarketSnapshotListener
import org.springframework.core.annotation.AnnotationAwareOrderComparator
import java.time.Instant
import java.util.logging.Logger

class MarketSnapshotNotifier(
        snapshotListeners: List<MarketSnapshotListener>,
        private val filterService: MarketFilterService,
        private val handlers: List<BetCommandHandler>,
        private val disabledListeners: Set<String>
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
                        .filter { it.javaClass.simpleName !in disabledListeners }
                        .flatMap { it.onMarketSnapshot(MarketSnapshotEvent(snapshot, account)) }

                return toCollectedBets(callHandlers(bets))
            }
        }
        return CollectedBets(emptyList(), emptyList(), emptyList())
    }

    private fun validateOpenDate(market: Market) {
        val currDate = Instant.now()
        val openDate = market.event.openDate
        check(currDate.isBefore(openDate)) { "current $currDate open date $openDate" }
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