package cz.fb.manaus.reactor.charge

import com.google.common.collect.LinkedListMultimap
import cz.fb.manaus.core.model.Bet
import cz.fb.manaus.core.model.MarketSnapshot
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.provider.ExchangeProvider
import cz.fb.manaus.reactor.betting.AmountAdviser
import cz.fb.manaus.reactor.price.Fairness
import cz.fb.manaus.reactor.price.ProbabilityCalculator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ChargeGrowthForecaster {

    @Autowired
    private lateinit var simulator: MarketChargeSimulator
    @Autowired
    private lateinit var probabilityCalculator: ProbabilityCalculator
    @Autowired
    private lateinit var amountAdviser: AmountAdviser
    @Autowired
    private lateinit var exchangeProvider: ExchangeProvider


    private fun convertBetData(currentBets: List<Bet>): LinkedListMultimap<Long, Price> {
        val bets = LinkedListMultimap.create<Long, Price>()
        for (bet in currentBets) {
            val price = bet.requestedPrice.price
            val matchedAmount = bet.matchedAmount
            val side = bet.requestedPrice.side
            bets.put(bet.selectionId, Price(price, matchedAmount, side))
        }
        return bets
    }

    fun getForecast(selectionId: Long, betSide: Side,
                    snapshot: MarketSnapshot, fairness: Fairness): Double? {
        if (exchangeProvider.isPerMarketCharge) {
            val fairnessSide = fairness.moreCredibleSide
            if (fairnessSide.isPresent) {
                val sideFairness = fairness.get(fairnessSide.get())
                val probabilities = probabilityCalculator.fromFairness(
                        sideFairness.asDouble, fairnessSide.get(), snapshot.marketPrices)
                val marketPrices = snapshot.marketPrices
                val bets = convertBetData(snapshot.currentBets)
                val runnerPrices = marketPrices.getRunnerPrices(selectionId)
                val winnerCount = marketPrices.winnerCount
                val oldCharge = simulator.getChargeMean(winnerCount, exchangeProvider.chargeRate, probabilities, bets)
                val bestPrice = runnerPrices.getHomogeneous(betSide.opposite).bestPrice
                if (bestPrice.isPresent) {
                    val price = bestPrice.get().price
                    val amount = amountAdviser.amount
                    bets.put(selectionId, Price(price, amount, betSide))
                    val newCharge = simulator.getChargeMean(winnerCount, exchangeProvider.chargeRate, probabilities, bets)
                    val result = newCharge / oldCharge
                    return result
                }
            }
        }
        return null
    }

}
