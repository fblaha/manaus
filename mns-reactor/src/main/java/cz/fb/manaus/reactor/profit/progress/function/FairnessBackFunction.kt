package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.price.FairnessPolynomialCalculator
import cz.fb.manaus.reactor.price.getBestPrices
import org.springframework.stereotype.Component

@Component
object FairnessBackFunction : ProgressFunction {

    override fun invoke(bet: RealizedBet): Double? {
        val marketPrices = bet.betAction.runnerPrices
        val bestPrices = getBestPrices(marketPrices, Side.BACK)
        return FairnessPolynomialCalculator.getFairness(1, bestPrices)
    }
}
