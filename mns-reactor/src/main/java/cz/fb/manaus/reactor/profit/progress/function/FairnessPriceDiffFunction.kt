package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.price.FairnessPolynomialCalculator
import cz.fb.manaus.reactor.price.Pricing
import cz.fb.manaus.reactor.price.getRunnerPrices
import org.springframework.stereotype.Component
import kotlin.math.abs

@Component
object FairnessPriceDiffFunction : ProgressFunction {

    override fun invoke(bet: RealizedBet): Double? {
        val marketPrices = bet.betAction.runnerPrices
        val fairness = FairnessPolynomialCalculator.getFairness(marketPrices)
        val layFairness = fairness[Side.LAY]
        val backFairness = fairness[Side.BACK]
        return if (layFairness != null && backFairness != null) {
            val runnerPrices = getRunnerPrices(marketPrices, bet.settledBet.selectionId)
            val layBest = runnerPrices.by(Side.LAY).bestPrice
            val backBest = runnerPrices.by(Side.BACK).bestPrice
            val layPrice = layBest!!.price
            val backPrice = backBest!!.price
            val fairnessLayFairPrice = Pricing.getFairnessFairPrice(layPrice, layFairness)
            val fairnessBackFairPrice = Pricing.getFairnessFairPrice(backPrice, backFairness)
            abs(fairnessBackFairPrice - fairnessLayFairPrice)
        } else {
            null
        }
    }

}
