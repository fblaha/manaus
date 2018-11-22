package cz.fb.manaus.reactor.betting.action

import com.google.common.base.Preconditions.checkArgument
import com.google.common.base.Preconditions.checkState
import com.google.common.base.Splitter
import com.google.common.collect.Comparators
import cz.fb.manaus.core.model.*

object BetUtils {

    fun getCurrentActions(betActions: List<BetAction>): List<BetAction> {
        val lastUpdates = mutableListOf<BetAction>()
        val first = betActions.first()
        for (bet in betActions) {
            validate(first, bet)
            if (bet.betActionType != BetActionType.UPDATE) lastUpdates.clear()
            lastUpdates.add(bet)
        }
        checkState(Comparators.isInStrictOrder(lastUpdates, compareBy { it.time }))
        return lastUpdates
    }

    private fun validate(first: BetAction, second: BetAction) {
        checkArgument(first.price.side === second.price.side)
        checkArgument(first.selectionId == second.selectionId)
    }

    fun getUnknownBets(bets: List<Bet>, myBets: Set<String>): List<Bet> {
        return bets.filter { it.betId !in myBets }
    }

    fun parseProposers(proposers: String): List<String> {
        return Splitter.on(',').omitEmptyStrings().trimResults().splitToList(proposers)
    }

    fun limitBetAmount(ceiling: Double, bet: RealizedBet): RealizedBet {
        val newBetPrice = limitPriceAmount(ceiling, bet.settledBet.price)
        var result = bet
        if (newBetPrice != null) {
            val amount = bet.settledBet.price.amount
            val rate = ceiling / amount
            val profitAndLoss = rate * bet.settledBet.profitAndLoss

            result = result.copy(settledBet = result.settledBet.copy(profitAndLoss = profitAndLoss))
        }
        val newActionPrice = limitPriceAmount(ceiling, bet.betAction.price)
        if (newActionPrice != null) {
            result = result.copy(betAction = result.betAction.copy(price = newActionPrice))
        }
        return result
    }

    private fun limitPriceAmount(ceiling: Double, origPrice: Price): Price? {
        val amount = origPrice.amount
        if (ceiling < amount) {
            return origPrice.copy(amount = ceiling)
        }
        return null
    }
}
