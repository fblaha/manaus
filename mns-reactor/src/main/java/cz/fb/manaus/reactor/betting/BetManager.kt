package cz.fb.manaus.reactor.betting

import com.google.common.base.Preconditions.checkState
import cz.fb.manaus.core.manager.MarketFilterService
import cz.fb.manaus.core.model.*
import cz.fb.manaus.reactor.betting.action.ActionSaver
import cz.fb.manaus.reactor.betting.action.BetActionListener
import cz.fb.manaus.reactor.betting.action.BetUtils
import cz.fb.manaus.reactor.betting.listener.MarketSnapshotListener
import cz.fb.manaus.reactor.price.AbstractPriceFilter
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.AnnotationAwareOrderComparator
import org.springframework.stereotype.Service
import java.util.*
import java.util.Objects.requireNonNull
import java.util.logging.Level
import java.util.logging.Logger

@Lazy
@Service
@Profile(ManausProfiles.DB)
class BetManager @Autowired
constructor(@Value(DISABLED_LISTENERS_EL) rawDisabledListeners: String?) {
    private val disabledListeners: Set<String>
    @Autowired
    private lateinit var betUtils: BetUtils
    @Autowired
    private lateinit var filterService: MarketFilterService
    @Autowired
    private lateinit var priceFilter: Optional<AbstractPriceFilter>
    @Autowired
    private lateinit var actionSaver: ActionSaver
    @Autowired(required = false)
    private val actionListeners = emptyList<BetActionListener>()
    private var marketSnapshotListeners: List<MarketSnapshotListener> = mutableListOf()


    init {
        this.disabledListeners = rawDisabledListeners?.split(',')?.toSet() ?: emptySet()
    }

    @Autowired(required = false)
    fun setMarketSnapshotListeners(marketSnapshotListeners: List<MarketSnapshotListener>) {
        requireNonNull(marketSnapshotListeners)
        this.marketSnapshotListeners = marketSnapshotListeners.sortedWith(AnnotationAwareOrderComparator.INSTANCE)
    }

    fun fire(snapshot: MarketSnapshot,
             myBets: Set<String>,
             accountMoney: Optional<AccountMoney>,
             categoryBlacklist: Set<String>): CollectedBets {
        val marketPrices = snapshot.marketPrices
        filterPrices(marketPrices)

        val reciprocal = marketPrices.getReciprocal(Side.BACK)
        val market = marketPrices.market
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
        val currDate = Date()
        val openDate = market.event.openDate
        checkState(currDate.before(openDate),
                "current %s, open date %s", currDate, openDate)
    }

    private fun saveActions(commands: List<BetCommand>) {
        val actions = commands.map { it.action }
        actions.forEach { actionSaver.saveAction(it) }
        actionListeners.forEach { listener -> actions.forEach { listener.onAction(it) } }
        commands.forEach { it.bet.actionId = it.action.id }
    }

    private fun filterPrices(marketPrices: MarketPrices) {
        if (priceFilter.isPresent) {
            for (runnerPrices in marketPrices.runnerPrices) {
                val prices = runnerPrices.prices.toList()
                val filtered = priceFilter.get().filter(prices)
                runnerPrices.prices = filtered
            }
        } else {
            log.log(Level.WARNING, "No price filtering configured.")
        }
    }

    private fun checkMarket(myBets: Set<String>, market: Market, reciprocal: OptionalDouble, categoryBlacklist: Set<String>): Boolean {
        return reciprocal.isPresent && filterService.accept(market, !myBets.isEmpty(), categoryBlacklist)
    }

    companion object {
        const val DISABLED_LISTENERS_EL = "#{systemEnvironment['MNS_DISABLED_LISTENERS']}"
        private val log = Logger.getLogger(BetManager::class.java.simpleName)
    }
}