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


data class MarketSnapshotCrate(
        var prices: MarketPrices,
        var bets: List<Bet>,
        var categoryBlacklist: Set<String>,
        var money: AccountMoney?,
        var scanTime: Int = 0
)

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
            val bets = snapshotCrate.bets
            betMetricUpdater.update(snapshotCrate.scanTime.toLong(), bets)
            val marketSnapshot = MarketSnapshot.from(marketPrices, bets, null)
            val myBets = actionDao.getBetActionIds(id, OptionalLong.empty(), Optional.empty<Side>())
            val collectedBets = manager.fire(marketSnapshot, myBets,
                    snapshotCrate.money, snapshotCrate.categoryBlacklist)
            return if (collectedBets.isEmpty) {
                ResponseEntity.noContent().build<Any>()
            } else {
                ResponseEntity.ok<CollectedBets>(collectedBets)
            }
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
        Preconditions.checkState(!snapshotCrate.prices.runnerPrices.isEmpty())
    }

    private fun logException(snapshot: MarketSnapshotCrate, e: RuntimeException) {
        log.log(Level.SEVERE, "Error emerged for ''{0}''", snapshot)
        log.log(Level.SEVERE, "fix it!", e)
    }

    companion object {
        private val log = Logger.getLogger(MarketSnapshotController::class.java.simpleName)
    }
}
