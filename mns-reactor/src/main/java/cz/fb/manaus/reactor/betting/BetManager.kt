package cz.fb.manaus.reactor.betting

import com.google.common.base.Preconditions.checkState
import cz.fb.manaus.core.manager.MarketFilterService
import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.repository.BetActionRepository
import cz.fb.manaus.reactor.betting.action.BetActionListener
import cz.fb.manaus.reactor.betting.action.BetUtils
import cz.fb.manaus.reactor.betting.listener.MarketSnapshotListener
import cz.fb.manaus.reactor.price.PriceFilter
import cz.fb.manaus.reactor.price.getReciprocal
import org.springframework.core.annotation.AnnotationAwareOrderComparator
import java.time.Instant
import java.util.logging.Level
import java.util.logging.Logger

class BetManager(
        private val filterService: MarketFilterService,
        private val priceFilter: PriceFilter?,
        private val betActionRepository: BetActionRepository,
        private val actionListeners: List<BetActionListener>,
        private val disabledListeners: Set<String>,
        private val sortedSnapshotListeners: List<MarketSnapshotListener>) {

    private val log = Logger.getLogger(BetManager::class.java.simpleName)

    fun fire(snapshot: MarketSnapshot,
             myBets: Set<String>,
             accountMoney: AccountMoney?,
             categoryBlacklist: Set<String>): CollectedBets {
        val marketPrices = filterPrices(snapshot.runnerPrices)

        val reciprocal = getReciprocal(marketPrices, Side.BACK)
        val market = snapshot.market
        val collector = BetCollector()

        if (checkMarket(myBets, market, reciprocal, categoryBlacklist)) {
            validateOpenDate(market)

            val unknownBets = BetUtils.getUnknownBets(snapshot.currentBets, myBets)
            unknownBets.forEach { bet -> log.log(Level.WARNING, "unknown bet ''{0}''", bet) }
            if (unknownBets.isEmpty()) {
                sortedSnapshotListeners
                        .filter { it.javaClass.simpleName !in disabledListeners }
                        .forEach { it.onMarketSnapshot(snapshot, collector, accountMoney, categoryBlacklist) }
                saveActions(collector.getToPlace())
                saveActions(collector.getToUpdate())
            }
        }
        return collector.toCollectedBets()
    }

    private fun validateOpenDate(market: Market) {
        val currDate = Instant.now()
        val openDate = market.event.openDate
        checkState(currDate.isBefore(openDate),
                "current %s, open date %s", currDate, openDate)
    }

    private fun saveActions(commands: List<BetCommand>) {
        for (command in commands) {
            saveAction(command)
            actionListeners.forEach { it.onAction(command.action) }
        }
    }

    private fun saveAction(command: BetCommand) {
        val actionId = betActionRepository.save(command.action)
        command.bet = command.bet.copy(actionId = actionId)
    }

    private fun filterPrices(marketPrices: List<RunnerPrices>): List<RunnerPrices> {
        return if (priceFilter == null) {
            log.log(Level.WARNING, "No price filtering configured.")
            marketPrices
        } else {
            marketPrices.map { it.copy(prices = priceFilter.filter(it.prices)) }
        }
    }

    private fun checkMarket(myBets: Set<String>, market: Market, reciprocal: Double?, categoryBlacklist: Set<String>): Boolean {
        return reciprocal != null && filterService.accept(market, myBets.isNotEmpty(), categoryBlacklist)
    }

    companion object {
        fun create(betActionRepository: BetActionRepository,
                   filterService: MarketFilterService,
                   priceFilter: PriceFilter?,
                   disabledListeners: Set<String>,
                   snapshotListeners: List<MarketSnapshotListener>,
                   actionListeners: List<BetActionListener>): BetManager {

            val sortedSnapshotListeners: List<MarketSnapshotListener> =
                    snapshotListeners.sortedWith(AnnotationAwareOrderComparator.INSTANCE)

            return BetManager(filterService,
                    priceFilter,
                    betActionRepository,
                    actionListeners,
                    disabledListeners,
                    sortedSnapshotListeners)
        }
    }
}