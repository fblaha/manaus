package cz.fb.manaus.reactor

import cz.fb.manaus.core.model.*
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.listener.BetEventFactory
import cz.fb.manaus.reactor.price.Fairness
import cz.fb.manaus.reactor.price.FairnessPolynomialCalculator
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit


@Component
class BetEventTestFactory(
        private val calculator: FairnessPolynomialCalculator,
        private val pricesTestFactory: PricesTestFactory,
        private val betEventFactory: BetEventFactory
) {

    fun newUpdateBetEvent(side: Side, marketPrices: List<RunnerPrices>): BetEvent {
        val oldBet = Bet(betId = betAction.betId,
                marketId = market.id,
                selectionId = SEL_HOME,
                requestedPrice = Price(5.0, 5.0, side),
                placedDate = Instant.now())
        val event = newBetEvent(side, marketPrices, oldBet)
        return event.copy(proposedPrice = oldBet.requestedPrice)
    }

    fun newBetEvent(side: Side, marketPrices: List<RunnerPrices>, oldBet: Bet?): BetEvent {
        val fairness = Fairness(0.9, 1.1)
        val snapshot = MarketSnapshot(
                runnerPrices = marketPrices,
                market = market,
                currentBets = oldBet?.let { listOf(it) }.orEmpty()
        )
        return betEventFactory.create(SideSelection(side, SEL_HOME), snapshot, fairness, mbAccount)
    }

    fun newBetEvent(side: Side, bestBack: Double, bestLay: Double): BetEvent {
        val snapshot = newSnapshot(side, bestBack, bestLay)
        val fairness = calculator.getFairness(snapshot.runnerPrices)
        val selectionId = snapshot.runnerPrices.first().selectionId
        return betEventFactory.create(SideSelection(side, selectionId), snapshot, fairness, mbAccount)
    }

    private fun newSnapshot(side: Side, bestBack: Double, bestLay: Double): MarketSnapshot {
        val marketPrices = pricesTestFactory.newMarketPrices(bestBack, bestLay, 3.0)
        val runnerPrices = marketPrices.first()
        val selectionId = runnerPrices.selectionId
        val bestPrice = runnerPrices.getHomogeneous(side.opposite).bestPrice
        val bets = if (bestPrice != null) {
            val marketId = "marketId"
            val price = bestPrice.price
            val requestedPrice = Price(price, mbProvider.minAmount, side.opposite)
            val date = Instant.now().minus(2, ChronoUnit.HOURS)
            val counterBet = Bet("1", marketId, selectionId, requestedPrice, date, mbProvider.minAmount)
            listOf(counterBet)
        } else emptyList()
        return MarketSnapshot(marketPrices, market, bets)
    }

}
