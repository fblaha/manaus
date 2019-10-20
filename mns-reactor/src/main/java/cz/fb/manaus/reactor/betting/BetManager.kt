package cz.fb.manaus.reactor.betting

import cz.fb.manaus.core.manager.MarketFilterService
import cz.fb.manaus.core.model.Account
import cz.fb.manaus.core.model.CollectedBets
import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.model.MarketSnapshot
import cz.fb.manaus.core.repository.BetActionRepository
import cz.fb.manaus.reactor.betting.action.BetActionListener
import cz.fb.manaus.reactor.betting.action.BetUtils
import cz.fb.manaus.reactor.betting.listener.MarketSnapshotListener
import org.springframework.core.annotation.AnnotationAwareOrderComparator
import java.time.Instant
import java.util.logging.Logger

class BetManager(
        snapshotListeners: List<MarketSnapshotListener>,
        private val filterService: MarketFilterService,
        private val betActionRepository: BetActionRepository,
        private val actionListeners: List<BetActionListener>,
        private val disabledListeners: Set<String>) {

    private val sortedSnapshotListeners: List<MarketSnapshotListener> =
            snapshotListeners.sortedWith(AnnotationAwareOrderComparator.INSTANCE)

    private val log = Logger.getLogger(BetManager::class.simpleName)

    fun fire(snapshot: MarketSnapshot, myBets: Set<String>, account: Account): CollectedBets {

        val market = snapshot.market
        val collector = BetCollector()

        if (filterService.accept(market, myBets.isNotEmpty(), account.provider.capabilityPredicate)) {
            validateOpenDate(market)

            val unknownBets = BetUtils.getUnknownBets(snapshot.currentBets, myBets)
            unknownBets.forEach { log.warning { "unknown bet '$it'" } }
            if (unknownBets.isEmpty()) {
                sortedSnapshotListeners
                        .filter { it.javaClass.simpleName !in disabledListeners }
                        .forEach { it.onMarketSnapshot(snapshot, collector, account) }
                saveActions(collector.getToPlace())
                saveActions(collector.getToUpdate())
            }
        }
        return collector.toCollectedBets()
    }

    private fun validateOpenDate(market: Market) {
        val currDate = Instant.now()
        val openDate = market.event.openDate
        check(currDate.isBefore(openDate)) { "current $currDate open date $openDate" }
    }

    private fun saveActions(commands: List<BetCommand>) {
        for (command in commands) {
            val actionId = betActionRepository.idSafeSave(command.action)
            command.bet = command.bet.copy(actionId = actionId)
            actionListeners.forEach { it.onAction(command.action) }
        }
    }
}