package cz.fb.manaus.reactor.betting

import com.google.common.base.Preconditions.checkState
import com.google.common.collect.Table
import cz.fb.manaus.core.model.*
import cz.fb.manaus.reactor.price.Fairness
import java.util.*

open class BetContext(open val side: Side,
                      val selectionId: Long,
                      val accountMoney: AccountMoney?,
                      open val chargeGrowthForecast: Double?,
                      private val marketSnapshot: MarketSnapshot,
                      val fairness: Fairness) {

    open val properties: MutableMap<String, String> = mutableMapOf()
    open val runnerPrices: RunnerPrices = marketSnapshot.marketPrices.getRunnerPrices(selectionId)
    open val marketPrices: MarketPrices = marketSnapshot.marketPrices

    open val actualTradedVolume: TradedVolume? =
            if (marketSnapshot.tradedVolume != null) {
                marketSnapshot.tradedVolume!![selectionId]!!
            } else {
                null
            }

    open val oldBet: Bet? = marketSnapshot.coverage.get(side, selectionId)

    open val counterBet: Bet? = marketSnapshot.coverage.get(side.opposite, selectionId)

    open val coverage: Table<Side, Long, Bet> = marketSnapshot.coverage

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
                    val oldSide = this.oldBet!!.requestedPrice.side
                    checkState(oldSide === newSide)
                }
                if (this.counterBet != null) {
                    val otherSide = this.counterBet!!.requestedPrice.side
                    checkState(otherSide === newSide.opposite)
                }
            }
            field = value
        }

    fun createBetAction(): BetAction {
        val type = if (oldBet != null) BetActionType.UPDATE else BetActionType.PLACE
        val marketPrices = marketSnapshot.marketPrices
        val action = BetAction()
        action.betActionType = type
        action.actionDate = Date()
        action.market = marketPrices.market
        action.selectionId = selectionId
        action.marketPrices = marketPrices
        action.properties = properties
        action.price = newPrice
        return action
    }

    // TODO not used
    fun simulateSettledBet(): SettledBet {
        val action = createBetAction()
        val bet = SettledBet()
        bet.selectionId = action.selectionId
        bet.price = action.price
        bet.betAction = action
        bet.placed = action.actionDate
        return bet
    }


}
