package cz.fb.manaus.reactor

import cz.fb.manaus.core.model.*
import cz.fb.manaus.reactor.price.PriceService
import org.springframework.stereotype.Component


@Component
class PricesTestFactory(
        private val priceService: PriceService
) {

    fun newRunnerPrices(
            selectionId: Long,
            bestBack: Double,
            bestLay: Double,
            lastMatchedPrice: Double? = null
    ): RunnerPrices {
        val backBestPrice = Price(bestBack, 100.0, Side.BACK)
        val layBestPrice = Price(bestLay, 100.0, Side.LAY)
        return RunnerPrices(
                selectionId = selectionId,
                prices = listOf(
                        backBestPrice,
                        layBestPrice,
                        backBestPrice.copy(price = backBestPrice.price * 0.98),
                        backBestPrice.copy(price = backBestPrice.price * 0.95),
                        layBestPrice.copy(price = layBestPrice.price * 1.03),
                        layBestPrice.copy(price = layBestPrice.price * 1.07),
                ),
                lastMatchedPrice = lastMatchedPrice,
                matchedAmount = 10.0
        )
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
            val backRounded = Price.round(backPrice)
            val layPrice = priceService.downgrade(fairPrice, downgradeFraction, Side.BACK)
            val layRounded = Price.round(layPrice)
            val selectionId = SEL_HOME * (i + 1)
            val lastMatched = Price.round(fairPrice)
            runnerPrices.add(newRunnerPrices(selectionId, backRounded, layRounded, lastMatched))
        }
        return runnerPrices
    }
}
