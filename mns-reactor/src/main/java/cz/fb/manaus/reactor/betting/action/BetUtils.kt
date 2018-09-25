package cz.fb.manaus.reactor.betting.action

import com.google.common.base.Preconditions
import com.google.common.base.Preconditions.checkArgument
import com.google.common.base.Preconditions.checkState
import com.google.common.base.Splitter
import com.google.common.collect.Comparators
import cz.fb.manaus.core.model.*
import org.springframework.beans.BeanUtils
import org.springframework.stereotype.Component

@Component
class BetUtils {

    fun getCurrentActions(betActions: List<BetAction>): List<BetAction> {
        Preconditions.checkArgument(!betActions.isEmpty(), "missing bet actions")
        val lastUpdates = mutableListOf<BetAction>()
        val first = betActions[0]
        for (bet in betActions) {
            validate(first, bet)
            if (bet.betActionType != BetActionType.UPDATE) lastUpdates.clear()
            lastUpdates.add(bet)
        }
        checkState(Comparators.isInStrictOrder(lastUpdates, compareBy { it.actionDate }))
        return lastUpdates
    }

    private fun validate(first: BetAction, second: BetAction) {
        checkArgument(first.price.side === second.price.side)
        checkArgument(first.selectionId == second.selectionId)
    }

    fun getUnknownBets(bets: List<Bet>, myBets: Set<String>): List<Bet> {
        return bets.filter { bet -> !myBets.contains(bet.betId) }
    }

    fun parseProposers(proposers: String): List<String> {
        return Splitter.on(',').omitEmptyStrings().trimResults().splitToList(proposers)
    }

    fun limitBetAmount(ceiling: Double, bet: SettledBet): SettledBet {
        val newPrice = limitPriceAmount(ceiling, bet.price)
        if (newPrice != null) {
            val copy = SettledBet()
            BeanUtils.copyProperties(bet, copy)

            val amount = bet.price.amount
            val rate = ceiling / amount
            copy.profitAndLoss = rate * bet.profitAndLoss
            limitActionAmount(ceiling, copy)

            copy.price = newPrice
            return copy
        }
        limitActionAmount(ceiling, bet)
        return bet
    }

    private fun limitPriceAmount(ceiling: Double, origPrice: Price): Price? {
        val amount = origPrice.amount
        if (ceiling < amount) {
            val newPrice = Price()
            BeanUtils.copyProperties(origPrice, newPrice)
            newPrice.amount = ceiling
            return newPrice
        }
        return null
    }

    fun limitActionAmount(ceiling: Double, betCopy: SettledBet) {
        val orig = betCopy.betAction
        if (orig != null) {
            val actionCopy = BetAction()
            BeanUtils.copyProperties(orig, actionCopy)
            val newPrice = limitPriceAmount(ceiling, orig.price)
            newPrice?.let { actionCopy.price = it }
            betCopy.betAction = actionCopy
        }
    }
}
