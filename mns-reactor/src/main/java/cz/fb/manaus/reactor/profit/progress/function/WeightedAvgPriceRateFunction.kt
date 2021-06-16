package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.model.getWeightedMean
import cz.fb.manaus.reactor.price.getRunnerPrices
import org.springframework.stereotype.Component


@Component
object WeightedAvgPriceRateFunction : ProgressFunction {

    override fun invoke(bet: RealizedBet): Double {
        val runnerPrices = bet.betAction.runnerPrices
        val settledBet = bet.settledBet
        val side = settledBet.price.side
        val prices = getRunnerPrices(runnerPrices, settledBet.selectionId)
            .by(side)
        val price = settledBet.price.price
        val avg = getWeightedMean(prices.prices) ?: error("empty")
        return when (side) {
            Side.BACK -> price / avg
            Side.LAY -> avg / price
        }
    }
}
