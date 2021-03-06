package cz.fb.manaus.reactor

import cz.fb.manaus.core.model.*
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.createBetEvent
import cz.fb.manaus.reactor.price.Fairness
import cz.fb.manaus.reactor.price.FairnessPolynomialCalculator
import java.time.Instant
import java.time.temporal.ChronoUnit


object BetEventTestFactory {

    fun newUpdateBetEvent(side: Side, marketPrices: List<RunnerPrices>): BetEvent {
        val oldBet = Bet(
                betId = betAction.betId,
                marketId = market.id,
                selectionId = SEL_HOME,
                requestedPrice = Price(5.0, 5.0, side),
                placedDate = Instant.now()
        )
        val event = newBetEvent(side, marketPrices, oldBet.asTracked)
        return event.copy(proposedPrice = oldBet.requestedPrice)
    }

    fun newBetEvent(side: Side, marketPrices: List<RunnerPrices>, oldBet: TrackedBet?): BetEvent {
        val fairness = Fairness(0.9, 1.1)
        val snapshot = MarketSnapshot(
                runnerPrices = marketPrices,
                market = market,
                currentBets = oldBet?.let { listOf(it) }.orEmpty()
        )
        return createBetEvent(SideSelection(side, SEL_HOME), snapshot, mbAccount, fairness)
    }

    fun newBetEvent(side: Side, bestBack: Double, bestLay: Double): BetEvent {
        val snapshot = newSnapshot(side, bestBack, bestLay)
        val fairness = FairnessPolynomialCalculator.getFairness(snapshot.runnerPrices)
        val selectionId = snapshot.runnerPrices.first().selectionId
        return createBetEvent(SideSelection(side, selectionId), snapshot, mbAccount, fairness)
    }

    private fun newSnapshot(side: Side, bestBack: Double, bestLay: Double): MarketSnapshot {
        val marketPrices = PricesTestFactory.newMarketPrices(bestBack, bestLay, 3.0)
        val runnerPrices = marketPrices.first()
        val selectionId = runnerPrices.selectionId
        val bestPrice = runnerPrices.by(side.opposite).bestPrice
        val bets = if (bestPrice != null) {
            val marketId = "marketId"
            val price = bestPrice.price
            val requestedPrice = Price(price, mbProvider.minAmount, side.opposite)
            val date = Instant.now().minus(2, ChronoUnit.HOURS)
            val counterBet = Bet(
                betId = "1",
                marketId = marketId,
                selectionId = selectionId,
                requestedPrice = requestedPrice,
                    placedDate = date,
                    matchedAmount = mbProvider.minAmount
            ).asTracked

            listOf(counterBet)
        } else emptyList()
        return MarketSnapshot(marketPrices, market, bets)
    }

}
