package cz.fb.manaus.reactor

import cz.fb.manaus.core.model.*
import cz.fb.manaus.reactor.price.PriceService
import cz.fb.manaus.reactor.rounding.RoundingService
import cz.fb.manaus.reactor.rounding.decrement
import cz.fb.manaus.reactor.rounding.increment
import org.springframework.stereotype.Component


@Component
class PricesTestFactory(
        private val roundingService: RoundingService,
        private val priceService: PriceService
) {

    fun newRunnerPrices(selectionId: Long, bestBack: Double, bestLay: Double, lastMatchedPrice: Double? = null): RunnerPrices {
        val avgPrice = (bestBack + bestLay) / 2
        val lastMatched = lastMatchedPrice ?: roundingService.roundBet(avgPrice, bfProvider::matches)!!
        val backBestPrice = Price(bestBack, 100.0, Side.BACK)
        val layBestPrice = Price(bestLay, 100.0, Side.LAY)
        return RunnerPrices(
                selectionId = selectionId,
                prices = listOf(
                        backBestPrice,
                        layBestPrice,
                        roundingService.decrement(backBestPrice, 1, bfProvider.minPrice, bfProvider::matches)!!,
                        roundingService.decrement(backBestPrice, 2, bfProvider.minPrice, bfProvider::matches)!!,
                        roundingService.increment(layBestPrice, 1, bfProvider::matches)!!,
                        roundingService.increment(layBestPrice, 2, bfProvider::matches)!!),
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
            val backRounded = roundingService.roundBet(backPrice, bfProvider::matches)
            val layPrice = priceService.downgrade(fairPrice, downgradeFraction, Side.BACK)
            val layRounded = roundingService.roundBet(layPrice, bfProvider::matches)
            val selectionId = SEL_HOME * (i + 1)
            val lastMatched = roundingService.roundBet(fairPrice, bfProvider::matches)
            runnerPrices.add(newRunnerPrices(selectionId, backRounded!!, layRounded!!, lastMatched!!))
        }
        return runnerPrices
    }
}
