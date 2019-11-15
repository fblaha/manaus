package cz.fb.manaus.reactor.betting

import cz.fb.manaus.core.model.*
import java.time.Instant
import java.util.logging.Logger

data class BetEvent(
        val selectionId: Long,
        val side: Side,
        val market: Market,
        val marketPrices: List<RunnerPrices>,
        val coverage: Map<SideSelection, Bet>,
        val account: Account,
        val metrics: BetMetrics
) {

    private val log = Logger.getLogger(BetEvent::class.simpleName)

    private val sideSelection = SideSelection(side, selectionId)
    val runnerPrices: RunnerPrices = marketPrices.first { it.selectionId == selectionId }
    val oldBet: Bet? = coverage[sideSelection]
    val counterBet: Bet? = coverage[sideSelection.oppositeSide]
    val isCounterHalfMatched: Boolean = counterBet?.isHalfMatched ?: false
    val isOldMatched: Boolean = oldBet?.isMatched == true
    var proposers: Set<String> = emptySet()
    var newPrice: Price? = null
        set(value) {
            validateNewPrice(value)
            field = value
        }

    private fun validateNewPrice(value: Price?) {
        if (value != null) {
            val newSide = value.side
            check(side == newSide)
            if (oldBet != null) {
                val oldSide = oldBet.requestedPrice.side
                check(oldSide == newSide)
            }
            if (counterBet != null) {
                val otherSide = counterBet.requestedPrice.side
                check(otherSide == newSide.opposite)
            }
        }
    }

    val betAction: BetAction
        get() {
            val type = when (oldBet) {
                null -> BetActionType.PLACE
                else -> BetActionType.UPDATE
            }
            return BetAction(
                    selectionId = selectionId,
                    price = newPrice!!,
                    id = 0,
                    time = Instant.now(),
                    marketId = market.id,
                    runnerPrices = marketPrices,
                    betActionType = type,
                    chargeGrowth = metrics.chargeGrowthForecast,
                    proposers = proposers)
        }

    // TODO not used
    val simulatedBet: RealizedBet get() = simulate(betAction, market)

    val placeOrUpdate: BetCommand
        get() {
            val action = betAction
            val newPrice = newPrice!!

            val oldBet = oldBet
            log.info { "bet ${action.betActionType} action '$action'" }
            return if (oldBet != null) {
                BetCommand(oldBet replacePrice newPrice.price, action)
            } else {
                val market = market
                val bet = Bet(marketId = market.id,
                        placedDate = Instant.now(),
                        selectionId = runnerPrices.selectionId,
                        requestedPrice = newPrice)
                BetCommand(bet, action)
            }
        }

    val cancel: BetCommand?
        get() {
            if (oldBet != null && !oldBet.isMatched) {
                return BetCommand(oldBet, null)
            }
            return null
        }

}
