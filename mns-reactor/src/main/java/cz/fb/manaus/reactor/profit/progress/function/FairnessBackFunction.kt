package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.model.getBestPrices
import cz.fb.manaus.reactor.price.FairnessPolynomialCalculator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class FairnessBackFunction : ProgressFunction {

    @Autowired
    private lateinit var calculator: FairnessPolynomialCalculator

    override fun invoke(bet: RealizedBet): Double? {
        val marketPrices = bet.betAction.runnerPrices
        return calculator.getFairness(1, getBestPrices(marketPrices, Side.BACK))
    }

}
