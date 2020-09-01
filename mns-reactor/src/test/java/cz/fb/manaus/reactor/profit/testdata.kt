package cz.fb.manaus.reactor.profit

import cz.fb.manaus.core.model.*

fun toRealizedBet(settledBet: SettledBet): RealizedBet {
    val action = betAction.copy(
        price = settledBet.price,
        selectionId = settledBet.selectionId,
        betId = settledBet.id
    )
    return RealizedBet(settledBet, action, market)
}

internal fun generateBets(requestedSide: Side? = null): List<SettledBet> {
    val result = mutableListOf<SettledBet>()
    var price = 1.5
    while (price < 4) {
        addSideBets(result, price, Side.LAY, requestedSide)
        addSideBets(result, price + 0.1, Side.BACK, requestedSide)
        price += 0.02
    }
    return result
}

private fun addSideBets(result: MutableList<SettledBet>, price: Double, side: Side, requestedSide: Side?) {
    if (requestedSide == null || requestedSide == side) {
        result.add(
            drawSettledBet.copy(
                profitAndLoss = 5.0,
                price = Price(price, 4.0, side),
                id = "draw" + result.size.toString()

            )
        )
        result.add(
            homeSettledBet.copy(
                profitAndLoss = 5.0,
                price = Price(price, 4.0, side),
                id = "home" + result.size.toString()
            )
        )
    }
}
