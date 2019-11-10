package cz.fb.manaus.reactor.betting.action

import cz.fb.manaus.core.model.*

object BetUtils {

    fun getCurrentActions(betActions: List<BetAction>): List<BetAction> {
        validate(betActions)
        val updates = betActions.takeLastWhile { it.betActionType == BetActionType.UPDATE }
        return when (val place = betActions.getOrNull(betActions.size - updates.size - 1)) {
            null -> updates
            else -> listOf(place) + updates
        }
    }

    private fun validate(betActions: List<BetAction>) {
        val nextZip = betActions.zipWithNext()
        nextZip.forEach { check(it.first.time < it.second.time) }
        nextZip.forEach { check(it.first.selectionId == it.second.selectionId) }
        nextZip.forEach { check(it.first.price.side == it.second.price.side) }
    }

    fun getUnknownBets(bets: List<Bet>, myBets: Set<String>): List<Bet> {
        return bets.filter { it.betId !in myBets }
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
        return if (ceiling < amount) origPrice.copy(amount = ceiling) else null
    }
}
