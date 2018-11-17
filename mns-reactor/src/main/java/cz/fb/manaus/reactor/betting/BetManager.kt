package cz.fb.manaus.reactor.betting

import com.google.common.base.Preconditions.checkState
import cz.fb.manaus.core.manager.MarketFilterService
import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.repository.BetActionRepository
import cz.fb.manaus.reactor.betting.action.BetActionListener
import cz.fb.manaus.reactor.betting.action.BetUtils
import cz.fb.manaus.reactor.betting.listener.MarketSnapshotListener
import cz.fb.manaus.reactor.price.AbstractPriceFilter
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.AnnotationAwareOrderComparator
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.logging.Level
import java.util.logging.Logger

@Service
@Profile(ManausProfiles.DB)
class BetManager(@Value(DISABLED_LISTENERS_EL) rawDisabledListeners: String?) {
    private val disabledListeners: Set<String> = rawDisabledListeners?.split(',')?.toSet() ?: emptySet()
    @Autowired
    private lateinit var betUtils: BetUtils
    @Autowired
    private lateinit var filterService: MarketFilterService
    @Autowired
    private var priceFilter: AbstractPriceFilter? = null
    @Autowired
    private lateinit var betActionRepository: BetActionRepository
    @Autowired(required = false)
    private val actionListeners = emptyList<BetActionListener>()
    private var marketSnapshotListeners: List<MarketSnapshotListener> = mutableListOf()


    @Autowired(required = false)
    fun setMarketSnapshotListeners(marketSnapshotListeners: List<MarketSnapshotListener>) {
        this.marketSnapshotListeners = marketSnapshotListeners.sortedWith(AnnotationAwareOrderComparator.INSTANCE)
    }

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

            val unknownBets = betUtils.getUnknownBets(snapshot.currentBets, myBets)
            unknownBets.forEach { bet -> log.log(Level.WARNING, "unknown bet ''{0}''", bet) }
            if (unknownBets.isEmpty()) {
                for (listener in marketSnapshotListeners) {
                    if (!disabledListeners.contains(listener.javaClass.simpleName)) {
                        listener.onMarketSnapshot(snapshot, collector, accountMoney, categoryBlacklist)
                    }
                }
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
        val actions = commands.map { it.action }
        actions.forEach { betActionRepository.save(it) }
        actionListeners.forEach { listener -> actions.forEach { listener.onAction(it) } }
        commands.forEach { it.bet = it.bet.copy(actionId = it.action.id) }
    }

    private fun filterPrices(marketPrices: List<RunnerPrices>): List<RunnerPrices> {
        return if (priceFilter == null) {
            log.log(Level.WARNING, "No price filtering configured.")
            marketPrices
        } else {
            val filter = priceFilter!!
            marketPrices.map { it.copy(prices = filter.filter(it.prices)) }
        }
    }

    private fun checkMarket(myBets: Set<String>, market: Market, reciprocal: Double?, categoryBlacklist: Set<String>): Boolean {
        return reciprocal != null && filterService.accept(market, !myBets.isEmpty(), categoryBlacklist)
    }

    companion object {
        const val DISABLED_LISTENERS_EL = "#{systemEnvironment['MNS_DISABLED_LISTENERS']}"
        private val log = Logger.getLogger(BetManager::class.java.simpleName)
    }
}