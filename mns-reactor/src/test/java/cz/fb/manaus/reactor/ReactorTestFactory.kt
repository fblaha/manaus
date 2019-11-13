package cz.fb.manaus.reactor

import cz.fb.manaus.core.model.*
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.BetMetrics
import cz.fb.manaus.reactor.charge.ChargeGrowthForecaster
import cz.fb.manaus.reactor.price.Fairness
import cz.fb.manaus.reactor.price.FairnessPolynomialCalculator
import cz.fb.manaus.reactor.price.PriceService
import cz.fb.manaus.reactor.rounding.RoundingService
import cz.fb.manaus.reactor.rounding.decrement
import cz.fb.manaus.reactor.rounding.increment
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit


@Component
class ReactorTestFactory(
        private val roundingService: RoundingService,
        private val calculator: FairnessPolynomialCalculator,
        private val priceService: PriceService,
        private val forecaster: ChargeGrowthForecaster) {

    fun newUpdateBetContext(marketPrices: List<RunnerPrices>, side: Side): BetEvent {
        val oldBet = Bet(betId = "1",
                marketId = "1",
                selectionId = SEL_HOME,
                requestedPrice = Price(5.0, 5.0, side),
                placedDate = Instant.now())
        val context = newBetEvent(side, marketPrices, oldBet)
        context.newPrice = oldBet.requestedPrice
        return context
    }

    fun newBetEvent(side: Side, marketPrices: List<RunnerPrices>, oldBet: Bet?): BetEvent {
        val fairness = Fairness(0.9, 1.1)
        val snapshot = MarketSnapshot(
                runnerPrices = marketPrices,
                market = market,
                currentBets = oldBet?.let { listOf(it) }.orEmpty()
        )
        return newEvent(side, SEL_HOME, fairness, snapshot)
    }

    fun newBetEvent(side: Side, bestBack: Double, bestLay: Double): BetEvent {
        val snapshot = newSnapshot(side, bestBack, bestLay)
        val fairness = calculator.getFairness(snapshot.runnerPrices)
        val runnerPrices = snapshot.runnerPrices.first()
        val selectionId = runnerPrices.selectionId
        return newEvent(side, selectionId, fairness, snapshot)
    }

    fun newSnapshot(side: Side, bestBack: Double, bestLay: Double): MarketSnapshot {
        val marketPrices = newMarketPrices(bestBack, bestLay, 3.0)
        val runnerPrices = marketPrices.first()
        val selectionId = runnerPrices.selectionId
        val bestPrice = runnerPrices.getHomogeneous(side.opposite).bestPrice
        val bets = if (bestPrice != null) {
            val marketId = "marketId"
            val price = bestPrice.price
            val requestedPrice = Price(price, provider.minAmount, side.opposite)
            val date = Instant.now().minus(2, ChronoUnit.HOURS)
            val counterBet = Bet("1", marketId, selectionId, requestedPrice, date, provider.minAmount)
            listOf(counterBet)
        } else emptyList()
        return MarketSnapshot(marketPrices, market, bets)
    }

    private fun newEvent(side: Side, selectionId: Long, fairness: Fairness, snapshot: MarketSnapshot): BetEvent {
        val forecast = forecaster.getForecast(selectionId = selectionId,
                betSide = side,
                snapshot = snapshot,
                fairness = fairness,
                commission = account.provider.commission
        )
        val metrics = BetMetrics(
                chargeGrowthForecast = forecast,
                fairness = fairness,
                actualTradedVolume = snapshot.tradedVolume?.get(key = selectionId)
        )
        return BetEvent(
                market = snapshot.market,
                side = side,
                selectionId = selectionId,
                marketPrices = snapshot.runnerPrices,
                account = account,
                coverage = snapshot.coverage,
                metrics = metrics
        )
    }

    fun newRunnerPrices(selectionId: Long, bestBack: Double, bestLay: Double, lastMatchedPrice: Double? = null): RunnerPrices {
        val avgPrice = (bestBack + bestLay) / 2
        val lastMatched = lastMatchedPrice ?: roundingService.roundBet(avgPrice, provider::matches)!!
        val backBestPrice = Price(bestBack, 100.0, Side.BACK)
        val layBestPrice = Price(bestLay, 100.0, Side.LAY)
        return RunnerPrices(
                selectionId = selectionId,
                prices = listOf(
                        backBestPrice,
                        layBestPrice,
                        roundingService.decrement(backBestPrice, 1, provider.minPrice, provider::matches)!!,
                        roundingService.decrement(backBestPrice, 2, provider.minPrice, provider::matches)!!,
                        roundingService.increment(layBestPrice, 1, provider::matches)!!,
                        roundingService.increment(layBestPrice, 2, provider::matches)!!),
                lastMatchedPrice = lastMatchedPrice,
                matchedAmount = lastMatched)
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
            val backRounded = roundingService.roundBet(backPrice, provider::matches)
            val layPrice = priceService.downgrade(fairPrice, downgradeFraction, Side.BACK)
            val layRounded = roundingService.roundBet(layPrice, provider::matches)
            val selectionId = SEL_HOME * (i + 1)
            val lastMatched = roundingService.roundBet(fairPrice, provider::matches)
            runnerPrices.add(newRunnerPrices(selectionId, backRounded!!, layRounded!!, lastMatched!!))
        }
        return runnerPrices
    }
}
