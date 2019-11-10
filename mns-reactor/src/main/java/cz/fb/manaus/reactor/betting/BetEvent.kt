package cz.fb.manaus.reactor.betting

import cz.fb.manaus.core.model.*
import java.time.Instant

data class BetEvent(
        val selectionId: Long,
        val side: Side,
        val market: Market,
        val marketPrices: List<RunnerPrices>,
        val coverage: Map<SideSelection, Bet>,
        val account: Account,
        val metrics: BetMetrics
) {
    val runnerPrices: RunnerPrices = marketPrices.first { it.selectionId == selectionId }
    val oldBet: Bet? = coverage[SideSelection(side, selectionId)]
    val counterBet: Bet? = coverage[SideSelection(side.opposite, selectionId)]
    val isCounterHalfMatched: Boolean = counterBet?.isHalfMatched ?: false
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
}
