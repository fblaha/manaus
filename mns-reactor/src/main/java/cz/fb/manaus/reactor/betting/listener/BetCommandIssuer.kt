package cz.fb.manaus.reactor.betting.listener

import com.codahale.metrics.MetricRegistry
import cz.fb.manaus.core.model.Bet
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.reactor.betting.BetCommand
import cz.fb.manaus.reactor.betting.BetEvent
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.logging.Logger

@Component
class BetCommandIssuer(private val metricRegistry: MetricRegistry) {

    private val log = Logger.getLogger(BetCommandIssuer::class.simpleName)

    fun placeOrUpdate(event: BetEvent): BetCommand {
        val action = event.betAction
        val newPrice = event.newPrice!!

        val oldBet = event.oldBet
        log.info { "bet ${action.betActionType} action '$action'" }
        return if (oldBet != null) {
            // TODO remove it after debug
            DEBUG(newPrice, oldBet)
            BetCommand(oldBet replacePrice newPrice.price, action)
        } else {
            val market = event.market
            val bet = Bet(marketId = market.id,
                    placedDate = Instant.now(),
                    selectionId = event.runnerPrices.selectionId,
                    requestedPrice = newPrice)
            BetCommand(bet, action)
        }
    }

    private fun DEBUG(newPrice: Price, oldBet: Bet) {
        val old = oldBet.requestedPrice.price
        val diff = newPrice.price - old
        log.info { "DBG: diff '${diff / (old - 1)}' new price '$newPrice' old price '${oldBet.requestedPrice}'" }
    }

    fun tryCancel(oldBet: Bet?): BetCommand? {
        if (oldBet != null && !oldBet.isMatched) {
            metricRegistry.counter("bet.cancel").inc()
            return BetCommand(oldBet, null)
        }
        return null
    }
}
