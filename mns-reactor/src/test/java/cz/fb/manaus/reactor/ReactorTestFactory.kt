package cz.fb.manaus.reactor

import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.provider.ExchangeProvider
import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.betting.BetContextFactory
import cz.fb.manaus.reactor.price.Fairness
import cz.fb.manaus.reactor.price.FairnessPolynomialCalculator
import cz.fb.manaus.reactor.price.PriceService
import cz.fb.manaus.reactor.rounding.RoundingService
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit


@Component
class ReactorTestFactory(
        private var roundingService: RoundingService,
        private var calculator: FairnessPolynomialCalculator,
        private var priceService: PriceService,
        private var contextFactory: BetContextFactory,
        private var provider: ExchangeProvider) {

    fun newUpdateBetContext(marketPrices: List<RunnerPrices>, side: Side): BetContext {
        val oldBet = Bet(betId = "1",
                marketId = "1",
                selectionId = SEL_HOME,
                requestedPrice = Price(5.0, 5.0, side),
                placedDate = Instant.now())
        val context = newBetContext(side, marketPrices, oldBet)
        context.newPrice = oldBet.requestedPrice
        return context
    }

    fun newBetContext(side: Side, marketPrices: List<RunnerPrices>, oldBet: Bet?): BetContext {
        val fairness = Fairness(0.9, 1.1)

        val bets = mutableListOf<Bet>()
        oldBet?.let { bet -> bets.add(bet) }
        val snapshot = MarketSnapshot.from(marketPrices, market, bets)

        return contextFactory.create(side, SEL_HOME, snapshot, fairness)
    }

    fun newBetContext(side: Side, bestBack: Double, bestLay: Double): BetContext {
        val marketPrices = newMarketPrices(bestBack, bestLay, 3.0)
        val runnerPrices = marketPrices.first()
        val selectionId = runnerPrices.selectionId
        val bestPrice = runnerPrices.getHomogeneous(side.opposite).bestPrice
        val bets = mutableListOf<Bet>()
        if (bestPrice != null) {
            val marketId = "marketID"
            val price = bestPrice.price
            val requestedPrice = Price(price, provider.minAmount, side.opposite)
            val date = Instant.now().minus(2, ChronoUnit.HOURS)
            val counterBet = Bet("1", marketId, selectionId, requestedPrice, date, provider.minAmount)
            bets.add(counterBet)
        }
        val snapshot = MarketSnapshot.from(marketPrices, market, bets)
        return contextFactory.create(side, selectionId, snapshot,
                calculator.getFairness(marketPrices))
    }

    fun newRunnerPrices(selectionId: Long, bestBack: Double, bestLay: Double, lastMatchedPrice: Double? = null): RunnerPrices {
        val lastMatched = lastMatchedPrice ?: roundingService.roundBet((bestBack + bestLay) / 2)!!
        val backBestPrice = Price(bestBack, 100.0, Side.BACK)
        val layBestPrice = Price(bestLay, 100.0, Side.LAY)
        return RunnerPrices(selectionId, listOf(
                backBestPrice,
                layBestPrice,
                roundingService.decrement(backBestPrice, 1)!!,
                roundingService.decrement(backBestPrice, 2)!!,
                roundingService.increment(layBestPrice, 1)!!,
                roundingService.increment(layBestPrice, 2)!!),
                lastMatchedPrice, lastMatched)
    }

    fun newMarketPrices(betBack: Double, bestLay: Double, lastMatched: Double? = null): List<RunnerPrices> {
        val home = newRunnerPrices(SEL_HOME, betBack, bestLay, lastMatched)
        val draw = newRunnerPrices(SEL_DRAW, betBack, bestLay, lastMatched)
        val away = newRunnerPrices(SEL_AWAY, betBack, bestLay, lastMatched)
        return listOf(home, draw, away)
    }

    fun newMarketPrices(downgradeFraction: Double, probabilities: List<Double>): List<RunnerPrices> {
        val runnerPrices = mutableListOf<RunnerPrices>()
        for ((i, p) in probabilities.withIndex()) {
            val fairPrice = 1 / p
            val backPrice = priceService.downgrade(fairPrice, downgradeFraction, Side.LAY)
            val backRounded = roundingService.roundBet(backPrice)
            val layPrice = priceService.downgrade(fairPrice, downgradeFraction, Side.BACK)
            val layRounded = roundingService.roundBet(layPrice)
            val selectionId = SEL_HOME * (i + 1)
            val lastMatched = roundingService.roundBet(fairPrice)
            runnerPrices.add(newRunnerPrices(selectionId, backRounded!!, layRounded!!, lastMatched!!))
        }
        return runnerPrices
    }
}
