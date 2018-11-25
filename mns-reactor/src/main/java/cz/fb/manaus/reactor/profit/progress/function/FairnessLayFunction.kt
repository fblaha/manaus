package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.price.FairnessPolynomialCalculator
import cz.fb.manaus.reactor.price.getBestPrices
import org.springframework.stereotype.Component

@Component
class FairnessLayFunction(private val calculator: FairnessPolynomialCalculator) : ProgressFunction {

    override fun invoke(bet: RealizedBet): Double? {
        val marketPrices = bet.betAction.runnerPrices
        return calculator.getFairness(1, getBestPrices(marketPrices, Side.LAY))
    }

}
