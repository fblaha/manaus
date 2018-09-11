package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.price.FairnessPolynomialCalculator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class FairnessLayFunction : ProgressFunction {

    @Autowired
    private lateinit var calculator: FairnessPolynomialCalculator

    override fun apply(bet: SettledBet): OptionalDouble {
        val marketPrices = bet.betAction.marketPrices
        return calculator.getFairness(marketPrices.winnerCount.toDouble(), marketPrices.getBestPrices(Side.LAY))
    }

}
