package cz.fb.manaus.reactor.betting

import com.google.common.base.Preconditions.checkState
import com.google.common.collect.Table
import cz.fb.manaus.core.model.*
import cz.fb.manaus.reactor.price.Fairness
import java.util.*
import java.util.Objects.requireNonNull

open class BetContext internal constructor(open val side: Side, val selectionId: Long,
                                           val accountMoney: AccountMoney?,
                                           open val chargeGrowthForecast: Double?,
                                           private val marketSnapshot: MarketSnapshot,
                                           val fairness: Fairness) {
    open val properties: MutableMap<String, String> = mutableMapOf()
    var newPrice: Price? = null

    open val runnerPrices: RunnerPrices = marketSnapshot.marketPrices.getRunnerPrices(selectionId)

    open val marketPrices: MarketPrices = marketSnapshot.marketPrices

    open val actualTradedVolume: TradedVolume? =
            if (marketSnapshot.tradedVolume.isPresent) {
                marketSnapshot.tradedVolume.get()[selectionId]!!
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

    fun withNewPrice(newPrice: Price): BetContext {
        val newSide = requireNonNull(newPrice.side)
        checkState(side === newSide)
        val oldBet = oldBet
        if (oldBet != null) {
            val oldSide = requireNonNull(oldBet.requestedPrice.side)
            checkState(oldSide === newSide)
        }
        val counterBet = counterBet
        if (counterBet != null) {
            val otherSide = counterBet.requestedPrice.side
            checkState(otherSide === newSide.opposite)

        }
        this.newPrice = newPrice
        return this
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
