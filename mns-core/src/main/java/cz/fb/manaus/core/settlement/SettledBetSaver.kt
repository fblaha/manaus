package cz.fb.manaus.core.settlement

import com.codahale.metrics.MetricRegistry
import com.google.common.base.Preconditions
import cz.fb.manaus.core.dao.BetActionDao
import cz.fb.manaus.core.dao.SettledBetDao
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.temporal.ChronoUnit
import java.util.logging.Level
import java.util.logging.Logger

@Component
@Profile(ManausProfiles.DB)
class SettledBetSaver(private val settledBetDao: SettledBetDao,
                      private val betActionDao: BetActionDao,
                      private val metricRegistry: MetricRegistry) {

    fun saveBet(betId: String, settledBet: SettledBet): SaveStatus {
        if (!settledBetDao.getSettledBet(betId).isPresent) {
            settledBet.betAction = betActionDao.getBetAction(betId).orElse(null)
            return if (settledBet.betAction != null) {
                validate(settledBet)
                settledBetDao.saveOrUpdate(settledBet)
                metricRegistry.counter("settled.bet.new").inc()
                SaveStatus.OK
            } else {
                metricRegistry.counter("settled.bet.NO_ACTION").inc()
                log.log(Level.WARNING, "SETTLED_BET: no bet action for ''{0}''", settledBet)
                SaveStatus.NO_ACTION
            }
        } else {
            log.log(Level.INFO, "SETTLED_BET: action with id ''{0}'' already saved", betId)
            return SaveStatus.COLLISION
        }
    }

    private fun validate(bet: SettledBet) {
        validateTimes(bet)
        validatePrice(bet)
        validateSelection(bet)
    }

    private fun validatePrice(bet: SettledBet) {
        val requestedPrice = bet.betAction.price
        val price = bet.price
        if (!Price.priceEq(requestedPrice.price, price.price)) {
            log.log(Level.WARNING, "Different requested price ''{0}''", bet)
        }
    }

    private fun validateSelection(bet: SettledBet) {
        val selectionId = bet.betAction.selectionId
        Preconditions.checkArgument(selectionId == bet.selectionId,
                "action.selectionId != bet.selectionId")
    }

    private fun validateTimes(bet: SettledBet) {
        val placed = bet.placed
        if (placed != null) {
            val actionDate = bet.betAction.actionDate
            val openDate = bet.betAction.market.event.openDate
            val latency = actionDate.toInstant().until(placed.toInstant(), ChronoUnit.SECONDS)
            if (latency > 30) {
                log.log(Level.WARNING, "Too big latency for ''{0}''", bet)
            }
            if (placed.after(openDate)) {
                metricRegistry.counter("settled.bet.PLACED_AFTER_START").inc()
                log.log(Level.SEVERE, "Placed after open date ''{0}''", bet)
            }
        }
    }

    companion object {
        private val log = Logger.getLogger(SettledBetSaver::class.java.simpleName)
    }


}
