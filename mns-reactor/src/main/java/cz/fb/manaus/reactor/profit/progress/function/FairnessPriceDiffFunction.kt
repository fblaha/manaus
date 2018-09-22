package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.core.model.Side
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

    override fun invoke(bet: SettledBet): Double? {
        val marketPrices = bet.betAction.marketPrices
        val fairness = calculator.getFairness(marketPrices)
        val layFairness = fairness[Side.LAY]
        val backFairness = fairness[Side.BACK]
        return if (layFairness != null && backFairness != null) {
            val runnerPrices = marketPrices.getRunnerPrices(bet.selectionId)
            val layBest = runnerPrices.getHomogeneous(Side.LAY).bestPrice
            val backBest = runnerPrices.getHomogeneous(Side.BACK).bestPrice
            val layPrice = layBest.get().price
            val backPrice = backBest.get().price
            val fairnessLayFairPrice = priceService.getFairnessFairPrice(layPrice, layFairness)
            val fairnessBackFairPrice = priceService.getFairnessFairPrice(backPrice, backFairness)
            Math.abs(fairnessBackFairPrice - fairnessLayFairPrice)
        } else {
            null
        }
    }

}
