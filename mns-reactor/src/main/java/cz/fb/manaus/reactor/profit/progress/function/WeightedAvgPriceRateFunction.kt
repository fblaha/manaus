package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.reactor.price.getRunnerPrices
import cz.fb.manaus.reactor.price.getWeightedMean
import org.springframework.stereotype.Component

@Component
object WeightedAvgPriceRateFunction : ProgressFunction {

    override fun invoke(bet: RealizedBet): Double? {
        val runnerPrices = bet.betAction.runnerPrices
        val settledBet = bet.settledBet
        val prices = getRunnerPrices(runnerPrices, settledBet.selectionId)
                .getHomogeneous(settledBet.price.side)
        val price = settledBet.price.price
        val avg = getWeightedMean(prices.prices) ?: error("empty")
        return if (avg > price) avg / price else price / avg
    }
}
