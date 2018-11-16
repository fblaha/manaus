package cz.fb.manaus.core.settlement

import com.codahale.metrics.MetricRegistry
import com.google.common.base.Preconditions
import cz.fb.manaus.core.model.BetAction
import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.core.repository.BetActionRepository
import cz.fb.manaus.core.repository.MarketRepository
import cz.fb.manaus.core.repository.SettledBetRepository
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.temporal.ChronoUnit
import java.util.logging.Level
import java.util.logging.Logger

@Component
@Profile(ManausProfiles.DB)
class SettledBetSaver(private val settledBetRepository: SettledBetRepository,
                      private val betActionRepository: BetActionRepository,
                      private val marketRepository: MarketRepository,
                      private val metricRegistry: MetricRegistry) {

    fun saveBet(settledBet: SettledBet): SaveStatus {
        if (settledBetRepository.read(settledBet.id) == null) {
            val action = betActionRepository.findRecentBetAction(settledBet.id)
            return if (action != null) {
                val market = marketRepository.read(action.marketID)
                validate(settledBet, action, market!!)
                settledBetRepository.save(settledBet)
                metricRegistry.counter("settled.bet.new").inc()
                SaveStatus.OK
            } else {
                metricRegistry.counter("settled.bet.NO_ACTION").inc()
                log.log(Level.WARNING, "SETTLED_BET: no bet action for ''{0}''", settledBet)
                SaveStatus.NO_ACTION
            }
        } else {
            log.log(Level.INFO, "SETTLED_BET: action with id ''{0}'' already saved", settledBet.id)
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
        if (!Price.priceEq(requestedPrice.price, price.price)) {
            log.log(Level.WARNING, "Different requested price ''{0}''", bet)
        }
    }

    private fun validateSelection(bet: SettledBet, action: BetAction) {
        val selectionId = action.selectionID
        Preconditions.checkArgument(selectionId == bet.selectionId,
                "action.selectionId != bet.selectionId")
    }

    private fun validateTimes(bet: SettledBet, action: BetAction, market: Market) {
        val placed = bet.placed
        val actionDate = action.time
        val openDate = market.event.openDate
        val latency = actionDate.until(placed, ChronoUnit.SECONDS)
        if (latency > 30) {
            log.log(Level.WARNING, "Too big latency for ''{0}''", bet)
        }
        if (placed != null && placed.isAfter(openDate)) {
            metricRegistry.counter("settled.bet.PLACED_AFTER_START").inc()
            log.log(Level.SEVERE, "Placed after open date ''{0}''", bet)
        }
    }

    companion object {
        private val log = Logger.getLogger(SettledBetSaver::class.java.simpleName)
    }


}
