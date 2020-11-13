package cz.fb.manaus.rest

import com.google.common.util.concurrent.AtomicDouble
import cz.fb.manaus.core.batch.ActionLoader
import cz.fb.manaus.core.model.*
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


data class MarketEvent(
        var prices: List<RunnerPrices>,
        var bets: List<Bet>,
        var account: Account,
        val tradedVolume: Map<Long, TradedVolume>? = null,
        var scanTime: Long = 0
)

@Controller
@Profile(ManausProfiles.DB)
class MarketEventController(
        private val notifier: MarketSnapshotNotifier,
        private val marketRepository: MarketRepository,
        private val betMetricUpdater: MatchedBetMetricUpdater,
        private val actionLoader: ActionLoader
) {

    private val availableMoney: AtomicDouble by lazy { Metrics.gauge("mns_account_money_available", AtomicDouble()) }
    private val totalMoney: AtomicDouble by lazy { Metrics.gauge("mns_account_money_total", AtomicDouble()) }

    private val log = Logger.getLogger(MarketEventController::class.simpleName)

    @RequestMapping(value = ["/markets/{id}/event"], method = [RequestMethod.POST])
    fun onMarketEvent(@PathVariable id: String, @RequestBody marketEvent: MarketEvent): ResponseEntity<CollectedBets> {
        validateMarket(marketEvent)
        val account = marketEvent.account
        account.provider.validate()
        updateMoneyMetrics(account.money)
        Metrics.counter("mns_market_event_post").increment()
        try {
            val marketPrices = marketEvent.prices
            val market = marketRepository.read(id) ?: error("no such market $id")
            val bets = marketEvent.bets
            betMetricUpdater.update(marketEvent.scanTime, bets)
            val currentBets = bets.distinctBy { it.betId }.map { actionLoader.load(it) }
            val snapshot = MarketSnapshot(
                    runnerPrices = marketPrices,
                    market = market,
                    currentBets = currentBets,
                    tradedVolume = marketEvent.tradedVolume
            )
            val collectedBets = notifier.notify(MarketSnapshotEvent(snapshot, account))
            return toResponse(collectedBets)
        } catch (e: Exception) {
            Metrics.counter("mns_exception_event_count").increment()
            logException(marketEvent, e)
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
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(collectedBets)
        }
    }

    private fun validateMarket(marketEvent: MarketEvent) {
        check(marketEvent.prices.isNotEmpty())
    }

    private fun logException(snapshot: MarketEvent, e: Exception) {
        log.severe { "error occurred for '$snapshot'" }
        log.log(Level.SEVERE, "fix it!", e)
    }
}
