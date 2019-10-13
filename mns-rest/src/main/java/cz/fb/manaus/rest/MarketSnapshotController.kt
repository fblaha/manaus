package cz.fb.manaus.rest

import com.codahale.metrics.MetricRegistry
import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.repository.BetActionRepository
import cz.fb.manaus.core.repository.MarketRepository
import cz.fb.manaus.reactor.betting.BetManager
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import java.util.logging.Level
import java.util.logging.Logger


data class MarketSnapshotCrate(
        var prices: List<RunnerPrices>,
        var bets: List<Bet>,
        var account: Account,
        val tradedVolume: Map<Long, TradedVolume>? = null,
        var scanTime: Long = 0
)

@Controller
@Profile(ManausProfiles.DB)
class MarketSnapshotController(private val manager: BetManager,
                               private val marketRepository: MarketRepository,
                               private val betActionRepository: BetActionRepository,
                               private val metricRegistry: MetricRegistry,
                               private val betMetricUpdater: MatchedBetMetricUpdater) {

    private val log = Logger.getLogger(MarketSnapshotController::class.simpleName)

    @RequestMapping(value = ["/markets/{id}/snapshot"], method = [RequestMethod.POST])
    fun pushMarketSnapshot(@PathVariable id: String, @RequestBody snapshotCrate: MarketSnapshotCrate): ResponseEntity<CollectedBets> {
        validateMarket(snapshotCrate)
        metricRegistry.meter("market.snapshot.post").mark()
        try {
            val marketPrices = snapshotCrate.prices
            val market = marketRepository.read(id)!!
            val bets = snapshotCrate.bets
            betMetricUpdater.update(snapshotCrate.scanTime, bets)
            val marketSnapshot = MarketSnapshot.from(marketPrices, market, bets, snapshotCrate.tradedVolume)
            val myBets = betActionRepository.find(id).mapNotNull { it.betId }.toSet()
            val collectedBets = manager.fire(marketSnapshot, myBets, snapshotCrate.account)
            return toResponse(collectedBets)
        } catch (e: RuntimeException) {
            metricRegistry.counter("_SNAPSHOT_ERROR_").inc()
            logException(snapshotCrate, e)
            throw e
        }
    }

    private fun toResponse(collectedBets: CollectedBets): ResponseEntity<CollectedBets> {
        return if (collectedBets.isEmpty) {
            ResponseEntity.noContent().build<CollectedBets>()
        } else {
            ResponseEntity.ok(collectedBets)
        }
    }

    private fun validateMarket(snapshotCrate: MarketSnapshotCrate) {
        check(snapshotCrate.prices.isNotEmpty())
    }

    private fun logException(snapshot: MarketSnapshotCrate, e: RuntimeException) {
        log.severe { "error emerged for '$snapshot'" }
        log.log(Level.SEVERE, "fix it!", e)
    }
}
