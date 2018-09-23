package cz.fb.manaus.reactor.betting

import com.google.common.base.Preconditions.checkState
import com.google.common.collect.Table
import cz.fb.manaus.core.model.*
import cz.fb.manaus.reactor.price.Fairness
import java.util.*
import java.util.Objects.requireNonNull
import java.util.Optional.ofNullable

open class BetContext internal constructor(open val side: Side, val selectionId: Long,
                                           val accountMoney: AccountMoney?,
                                           open val chargeGrowthForecast: Double?,
                                           private val marketSnapshot: MarketSnapshot,
                                           val fairness: Fairness) {
    open val properties: MutableMap<String, String> = mutableMapOf()
    var newPrice = Optional.empty<Price>()
        private set

    open val runnerPrices: RunnerPrices = marketSnapshot.marketPrices.getRunnerPrices(selectionId)

    open val marketPrices: MarketPrices = marketSnapshot.marketPrices

    open val actualTradedVolume: Optional<TradedVolume> =
            if (marketSnapshot.tradedVolume.isPresent) {
                Optional.of(marketSnapshot.tradedVolume.get()[selectionId]!!)
            } else {
                Optional.empty()
            }

    open val oldBet: Optional<Bet> = ofNullable(marketSnapshot.coverage.get(side, selectionId))

    open val counterBet: Optional<Bet> = ofNullable(marketSnapshot.coverage.get(side.opposite, selectionId))

    open val coverage: Table<Side, Long, Bet> = marketSnapshot.coverage

    val isCounterHalfMatched: Boolean
        get() {
            val counterBet = counterBet
            return counterBet.map { it.isHalfMatched }.orElse(false)
        }

    fun withNewPrice(newPrice: Price): BetContext {
        val newSide = requireNonNull(newPrice.side)
        checkState(side === newSide)
        val oldBet = oldBet
        if (oldBet.isPresent) {
            val oldSide = requireNonNull(oldBet.get().requestedPrice.side)
            checkState(oldSide === newSide)
        }
        val counterBet = counterBet
        counterBet.ifPresent { bet ->
            val otherSide = requireNonNull(bet.requestedPrice.side)
            checkState(otherSide === newSide.opposite)
        }
        this.newPrice = Optional.of(newPrice)
        return this
    }

    fun createBetAction(): BetAction {
        val type = if (oldBet.isPresent) BetActionType.UPDATE else BetActionType.PLACE
        val marketPrices = marketSnapshot.marketPrices
        val action = BetAction()
        action.betActionType = type
        action.actionDate = Date()
        action.market = marketPrices.market
        action.selectionId = selectionId
        action.marketPrices = marketPrices
        action.properties = properties
        action.price = newPrice.get()
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
