package cz.fb.manaus.rest

import com.google.common.util.concurrent.AtomicDouble
import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.repository.BetActionRepository
import cz.fb.manaus.core.repository.MarketRepository
import cz.fb.manaus.reactor.betting.MarketSnapshotNotifier
import cz.fb.manaus.spring.ManausProfiles
import io.micrometer.core.instrument.Metrics
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
class MarketSnapshotController(private val notifier: MarketSnapshotNotifier,
                               private val marketRepository: MarketRepository,
                               private val actionRepository: BetActionRepository,
                               private val betMetricUpdater: MatchedBetMetricUpdater) {

    private val availableMoney: AtomicDouble by lazy { Metrics.gauge("account_money_available", AtomicDouble()) }
    private val totalMoney: AtomicDouble by lazy { Metrics.gauge("account_money_total", AtomicDouble()) }

    private val log = Logger.getLogger(MarketSnapshotController::class.simpleName)

    @RequestMapping(value = ["/markets/{id}/snapshot"], method = [RequestMethod.POST])
    fun pushMarketSnapshot(@PathVariable id: String, @RequestBody snapshotCrate: MarketSnapshotCrate): ResponseEntity<CollectedBets> {
        validateMarket(snapshotCrate)
        val account = snapshotCrate.account
        account.provider.validate()
        updateMoneyMetrics(account.money)
        Metrics.counter("market_snapshot_post").increment()
        try {
            val marketPrices = snapshotCrate.prices
            val market = marketRepository.read(id)!!
            val bets = snapshotCrate.bets
            betMetricUpdater.update(snapshotCrate.scanTime, bets)
            val snapshot = MarketSnapshot(marketPrices, market, bets, snapshotCrate.tradedVolume)
            val myBets = actionRepository.find(id).mapNotNull { it.betId }.toSet()
            val collectedBets = notifier.notify(snapshot, myBets, account)
            return toResponse(collectedBets)
        } catch (e: RuntimeException) {
            Metrics.counter("exception_snapshot_count").increment()
            logException(snapshotCrate, e)
            throw e
        }
    }

    private fun updateMoneyMetrics(money: AccountMoney?) {
        if (money != null) {
            availableMoney.set(money.available)
            totalMoney.set(money.total)
        }
    }

    private fun toResponse(collectedBets: CollectedBets): ResponseEntity<CollectedBets> {
        return if (collectedBets.empty) {
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
