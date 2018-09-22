package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.reactor.price.FairnessPolynomialCalculator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class LastMatchedFairnessFunction : ProgressFunction {

    @Autowired
    private lateinit var calculator: FairnessPolynomialCalculator

    override fun invoke(bet: SettledBet): Double? {
        val marketPrices = bet.betAction.marketPrices
        val lastMatched = marketPrices.runnerPrices.map { it.lastMatchedPrice }
        return calculator.getFairness(marketPrices.winnerCount.toDouble(), lastMatched)
    }
}
