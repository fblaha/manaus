package cz.fb.manaus.reactor.betting

import cz.fb.manaus.core.model.*
import java.time.Instant
import java.util.logging.Logger

data class BetEvent(
        val sideSelection: SideSelection,
        val market: Market,
        val marketPrices: List<RunnerPrices>,
        val coverage: Map<SideSelection, Bet>,
        val account: Account,
        val metrics: BetMetrics,
        val newPrice: Price? = null
) {

    private val log = Logger.getLogger(BetEvent::class.simpleName)

    val side: Side = sideSelection.side
    val runnerPrices: RunnerPrices = marketPrices.first { it.selectionId == sideSelection.selectionId }
    val oldBet: Bet? = coverage[sideSelection]
    val counterBet: Bet? = coverage[sideSelection.oppositeSide]
    val isOldMatched: Boolean = oldBet?.isMatched == true

    init {
        if (newPrice != null) {
            val newSide = newPrice.side
            check(sideSelection.side == newSide)
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

    fun betAction(proposers: Set<String>): BetAction {
        val type = when (oldBet) {
            null -> BetActionType.PLACE
            else -> BetActionType.UPDATE
        }
        return BetAction(
                selectionId = sideSelection.selectionId,
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
    val simulatedBet: RealizedBet get() = simulate(betAction(emptySet()), market)

    fun placeOrUpdate(proposers: Set<String>): BetCommand {
        val action = betAction(proposers)
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
