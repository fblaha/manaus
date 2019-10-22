package cz.fb.manaus.reactor.betting

import cz.fb.manaus.core.model.*
import cz.fb.manaus.reactor.price.Fairness
import java.time.Instant

data class Metrics(
        val chargeGrowthForecast: Double?,
        val fairness: Fairness,
        val actualTradedVolume: TradedVolume?
)


data class BetContext(
        val selectionId: Long,
        val side: Side,
        val market: Market,
        val marketPrices: List<RunnerPrices>,
        val coverage: Map<SideSelection, Bet>,
        val account: Account,
        val metrics: Metrics
) {
    val runnerPrices: RunnerPrices
        get() = marketPrices.first { it.selectionId == selectionId }

    val oldBet: Bet? = coverage[SideSelection(side, selectionId)]

    val counterBet: Bet? = coverage[SideSelection(side.opposite, selectionId)]

    val isCounterHalfMatched: Boolean
        get() {
            return counterBet?.isHalfMatched ?: false
        }

    var proposers: Set<String> = emptySet()

    var newPrice: Price? = null
        set(value) {
            if (value != null) {
                val newSide = value.side
                check(side === newSide)
                if (this.oldBet != null) {
                    val oldSide = this.oldBet.requestedPrice.side
                    check(oldSide === newSide)
                }
                if (this.counterBet != null) {
                    val otherSide = this.counterBet.requestedPrice.side
                    check(otherSide === newSide.opposite)
                }
            }
            field = value
        }

    fun createBetAction(): BetAction {
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
    fun simulateSettledBet(): RealizedBet {
        val market = market
        val action = createBetAction()
        val bet = SettledBet(
                id = "",
                selectionId = action.selectionId,
                selectionName = market.getRunner(action.selectionId).name,
                profitAndLoss = 0.0,
                commission = null,
                placed = action.time,
                matched = action.time,
                settled = Instant.now(),
                price = action.price
        )
        return RealizedBet(bet, action, market)
    }
}
