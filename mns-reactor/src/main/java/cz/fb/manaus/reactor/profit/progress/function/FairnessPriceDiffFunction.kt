package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.price.FairnessPolynomialCalculator
import cz.fb.manaus.reactor.price.PriceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class FairnessPriceDiffFunction : ProgressFunction {

    @Autowired
    private lateinit var calculator: FairnessPolynomialCalculator
    @Autowired
    private lateinit var priceService: PriceService

    override fun apply(bet: SettledBet): OptionalDouble {
        val marketPrices = bet.betAction.marketPrices
        val fairness = calculator.getFairness(marketPrices)
        return if (fairness.get(Side.LAY).isPresent && fairness.get(Side.BACK).isPresent) {
            val runnerPrices = marketPrices.getRunnerPrices(bet.selectionId)
            val layBest = runnerPrices.getHomogeneous(Side.LAY).bestPrice
            val backBest = runnerPrices.getHomogeneous(Side.BACK).bestPrice
            val layPrice = layBest.get().price
            val backPrice = backBest.get().price
            val fairnessLayFairPrice = priceService.getFairnessFairPrice(layPrice, fairness.get(Side.LAY).asDouble)
            val fairnessBackFairPrice = priceService.getFairnessFairPrice(backPrice, fairness.get(Side.BACK).asDouble)
            OptionalDouble.of(Math.abs(fairnessBackFairPrice - fairnessLayFairPrice))
        } else {
            OptionalDouble.empty()
        }
    }

}
