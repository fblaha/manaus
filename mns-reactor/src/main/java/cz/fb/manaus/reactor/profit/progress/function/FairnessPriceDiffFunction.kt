package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.model.getRunnerPrices
import cz.fb.manaus.reactor.price.FairnessPolynomialCalculator
import cz.fb.manaus.reactor.price.PriceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class FairnessPriceDiffFunction : ProgressFunction {

    @Autowired
    private lateinit var calculator: FairnessPolynomialCalculator
    @Autowired
    private lateinit var priceService: PriceService

    override fun invoke(bet: RealizedBet): Double? {
        val marketPrices = bet.betAction.runnerPrices
        val fairness = calculator.getFairness(marketPrices)
        val layFairness = fairness[Side.LAY]
        val backFairness = fairness[Side.BACK]
        return if (layFairness != null && backFairness != null) {
            val runnerPrices = getRunnerPrices(marketPrices, bet.settledBet.selectionId)
            val layBest = runnerPrices.getHomogeneous(Side.LAY).bestPrice
            val backBest = runnerPrices.getHomogeneous(Side.BACK).bestPrice
            val layPrice = layBest!!.price
            val backPrice = backBest!!.price
            val fairnessLayFairPrice = priceService.getFairnessFairPrice(layPrice, layFairness)
            val fairnessBackFairPrice = priceService.getFairnessFairPrice(backPrice, backFairness)
            Math.abs(fairnessBackFairPrice - fairnessLayFairPrice)
        } else {
            null
        }
    }

}
