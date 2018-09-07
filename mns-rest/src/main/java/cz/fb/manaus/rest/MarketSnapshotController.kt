package cz.fb.manaus.rest

import com.codahale.metrics.MetricRegistry
import com.google.common.base.Joiner
import com.google.common.base.Preconditions
import cz.fb.manaus.core.dao.BetActionDao
import cz.fb.manaus.core.dao.MarketDao
import cz.fb.manaus.core.model.*
import cz.fb.manaus.reactor.betting.BetManager
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger


@Controller
@Profile(ManausProfiles.DB)
class MarketSnapshotController {

    @Autowired
    private lateinit var manager: BetManager
    @Autowired
    private lateinit var marketDao: MarketDao
    @Autowired
    private lateinit var actionDao: BetActionDao
    @Autowired
    private lateinit var metricRegistry: MetricRegistry
    @Autowired
    private lateinit var betMetricUpdater: MatchedBetMetricUpdater

    @RequestMapping(value = ["/markets/{id}/snapshot"], method = [RequestMethod.POST])
    fun pushMarketSnapshot(@PathVariable id: String, @RequestBody snapshotCrate: MarketSnapshotCrate): ResponseEntity<*> {
        validateMarket(snapshotCrate)
        metricRegistry.meter("market.snapshot.post").mark()
        try {
            val marketPrices = snapshotCrate.prices
            marketDao.get(id).ifPresent { marketPrices.market = it }
            logMarket(marketPrices)
            val bets = Optional.ofNullable(snapshotCrate.bets).orElse(listOf())
            betMetricUpdater.update(snapshotCrate.scanTime.toLong(), bets)
            val marketSnapshot = MarketSnapshot.from(marketPrices, bets,
                    Optional.empty<Map<Long, TradedVolume>>())
            val myBets = actionDao.getBetActionIds(id, OptionalLong.empty(), Optional.empty<Side>())
            val collectedBets = manager.fire(marketSnapshot, myBets,
                    Optional.ofNullable(snapshotCrate.money),
                    Optional.ofNullable(snapshotCrate.categoryBlacklist).orElse(setOf()))
            return if (!collectedBets.isEmpty) {
                ResponseEntity.ok<CollectedBets>(collectedBets)
            } else ResponseEntity.noContent().build<Any>()
        } catch (e: RuntimeException) {
            metricRegistry.counter("_SNAPSHOT_ERROR_").inc()
            logException(snapshotCrate, e)
            throw e
        }

    }

    private fun logMarket(marketPrices: MarketPrices) {
        val market = marketPrices.market
        log.log(Level.INFO, "Market snapshot for ''{0}'' received",
                Joiner.on(" / ").join(market.event.name, market.name, market.id))
    }

    private fun validateMarket(snapshotCrate: MarketSnapshotCrate) {
        Objects.requireNonNull<MarketPrices>(snapshotCrate.prices)
        Objects.requireNonNull<Collection<RunnerPrices>>(snapshotCrate.prices.runnerPrices)
        Preconditions.checkState(!snapshotCrate.prices.runnerPrices.isEmpty())
    }

    private fun logException(snapshot: MarketSnapshotCrate, e: RuntimeException) {
        log.log(Level.SEVERE, "Error emerged for ''{0}''", snapshot)
        log.log(Level.SEVERE, "fix it!", e)
    }

    companion object {
        // TODO kotlin logging ?
        private val log = Logger.getLogger(MarketSnapshotController::class.java.simpleName)
    }
}

data class MarketSnapshotCrate(
        var prices: MarketPrices,
        var bets: List<Bet>,
        var categoryBlacklist: Set<String>,
        var money: AccountMoney,
        var scanTime: Int = 0
)

