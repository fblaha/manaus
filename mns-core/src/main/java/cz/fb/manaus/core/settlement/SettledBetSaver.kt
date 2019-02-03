package cz.fb.manaus.core.settlement

import com.codahale.metrics.MetricRegistry
import cz.fb.manaus.core.model.BetAction
import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.core.model.priceEq
import cz.fb.manaus.core.repository.BetActionRepository
import cz.fb.manaus.core.repository.MarketRepository
import cz.fb.manaus.core.repository.SettledBetRepository
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.temporal.ChronoUnit
import java.util.logging.Logger

@Component
@Profile(ManausProfiles.DB)
class SettledBetSaver(private val settledBetRepository: SettledBetRepository,
                      private val betActionRepository: BetActionRepository,
                      private val marketRepository: MarketRepository,
                      private val metricRegistry: MetricRegistry) {

    private val log = Logger.getLogger(SettledBetSaver::class.simpleName)

    fun saveBet(settledBet: SettledBet): SaveStatus {
        // TODO check bet equality and update
        if (settledBetRepository.read(settledBet.id) == null) {
            val action = betActionRepository.findRecentBetAction(settledBet.id)
            return if (action != null) {
                val market = marketRepository.read(action.marketId)
                validate(settledBet, action, market!!)
                settledBetRepository.save(settledBet)
                metricRegistry.counter("settled.bet.new").inc()
                SaveStatus.OK
            } else {
                metricRegistry.counter("settled.bet.NO_ACTION").inc()
                log.warning { "no bet action for '$settledBet'" }
                SaveStatus.NO_ACTION
            }
        } else {
            log.info { "settled bet with id '${settledBet.id}' already saved" }
            return SaveStatus.COLLISION
        }
    }

    private fun validate(bet: SettledBet, action: BetAction, market: Market) {
        validateTimes(bet, action, market)
        validatePrice(bet, action)
        validateSelection(bet, action)
    }

    private fun validatePrice(bet: SettledBet, action: BetAction) {
        val requestedPrice = action.price
        val price = bet.price
        if (!(requestedPrice.price priceEq price.price)) {
            log.warning { "different requested price '$requestedPrice' bet '$bet'" }
        }
    }

    private fun validateSelection(bet: SettledBet, action: BetAction) {
        val selectionId = action.selectionId
        require(selectionId == bet.selectionId) { "action.selectionId != bet.selectionId" }
    }

    private fun validateTimes(bet: SettledBet, action: BetAction, market: Market) {
        val placed = bet.placed
        val actionTime = action.time
        val openDate = market.event.openDate
        if (placed != null) {
            val latency = actionTime.until(placed, ChronoUnit.SECONDS)
            if (latency > 30) {
                log.warning { "too big latency $latency sec for '$bet'" }
            }
            if (placed.isAfter(openDate)) {
                metricRegistry.counter("settled.bet.PLACED_AFTER_START").inc()
                log.severe { "placed after open date '$bet'" }
            }
        }
    }
}
