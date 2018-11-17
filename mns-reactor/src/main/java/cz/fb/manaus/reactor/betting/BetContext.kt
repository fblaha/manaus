package cz.fb.manaus.reactor.betting

import com.google.common.base.Preconditions.checkState
import com.google.common.collect.Table
import cz.fb.manaus.core.model.*
import cz.fb.manaus.reactor.price.Fairness
import java.time.Instant

data class BetContext(
        val selectionId: Long,
        val side: Side,
        val market: Market,
        val marketPrices: List<RunnerPrices>,
        val chargeGrowthForecast: Double?,
        val coverage: Table<Side, Long, Bet>,
        val accountMoney: AccountMoney?,
        val fairness: Fairness,
        val actualTradedVolume: TradedVolume?
) {

    val runnerPrices: RunnerPrices
        get() = marketPrices.first { it.selectionId == selectionId }

    val properties: MutableMap<String, String> = mutableMapOf()

    val oldBet: Bet? = coverage.get(side, selectionId)

    val counterBet: Bet? = coverage.get(side.opposite, selectionId)

    val isCounterHalfMatched: Boolean
        get() {
            return counterBet?.isHalfMatched ?: false
        }

    var newPrice: Price? = null
        set(value) {
            if (value != null) {
                val newSide = value.side
                checkState(side === newSide)
                if (this.oldBet != null) {
                    val oldSide = this.oldBet.requestedPrice.side
                    checkState(oldSide === newSide)
                }
                if (this.counterBet != null) {
                    val otherSide = this.counterBet.requestedPrice.side
                    checkState(otherSide === newSide.opposite)
                }
            }
            field = value
        }

    fun createBetAction(): BetAction {
        val type = if (oldBet != null) BetActionType.UPDATE else BetActionType.PLACE
        return BetAction(
                selectionID = selectionId,
                price = newPrice!!,
                id = 0,
                time = Instant.now(),
                marketID = market.id,
                runnerPrices = marketPrices,
                properties = properties,
                betActionType = type)
    }

    // TODO not used
    fun simulateSettledBet(): RealizedBet {
        val market = market
        val action = createBetAction()
        val bet = SettledBet(
                selectionId = action.selectionID,
                price = action.price,
                placed = action.time,
                matched = action.time,
                settled = Instant.now(),
                profitAndLoss = 0.0,
                selectionName = market.getRunner(action.selectionID).name
        )
        return RealizedBet(bet, action, market)
    }
}
