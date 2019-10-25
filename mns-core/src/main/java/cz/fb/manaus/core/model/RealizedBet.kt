package cz.fb.manaus.core.model

import java.time.Instant

data class RealizedBet(val settledBet: SettledBet,
                       val betAction: BetAction,
                       val market: Market)

fun simulate(betAction: BetAction, market: Market): RealizedBet {
    val bet = SettledBet(
            id = "",
            selectionId = betAction.selectionId,
            selectionName = market.getRunner(betAction.selectionId).name,
            profitAndLoss = 0.0,
            commission = null,
            placed = betAction.time,
            matched = betAction.time,
            settled = Instant.now(),
            price = betAction.price
    )
    return RealizedBet(
            settledBet = bet,
            betAction = betAction,
            market = market
    )
}
