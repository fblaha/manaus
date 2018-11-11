package cz.fb.manaus.reactor.charge

import com.google.common.collect.LinkedListMultimap
import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.provider.ExchangeProvider
import cz.fb.manaus.reactor.betting.AmountAdviser
import cz.fb.manaus.reactor.price.Fairness
import cz.fb.manaus.reactor.price.ProbabilityCalculator
import org.springframework.stereotype.Component

@Component
class ChargeGrowthForecaster(
        private val simulator: MarketChargeSimulator,
        private val probabilityCalculator: ProbabilityCalculator,
        private val amountAdviser: AmountAdviser,
        private val exchangeProvider: ExchangeProvider) {

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
            if (fairnessSide != null) {
                val sideFairness = fairness[fairnessSide]!!
                val probabilities = probabilityCalculator.fromFairness(
                        sideFairness, fairnessSide, snapshot.runnerPrices)
                val marketPrices = snapshot.runnerPrices
                val bets = convertBetData(snapshot.currentBets)
                val runnerPrices = getRunnerPrices(marketPrices, selectionId)
                val oldCharge = simulator.getChargeMean(1, exchangeProvider.chargeRate, probabilities, bets)
                val bestPrice = runnerPrices.getHomogeneous(betSide.opposite).bestPrice
                if (bestPrice != null) {
                    val price = bestPrice.price
                    val amount = amountAdviser.amount
                    bets.put(selectionId, Price(price, amount, betSide))
                    val newCharge = simulator.getChargeMean(1, exchangeProvider.chargeRate, probabilities, bets)
                    return newCharge / oldCharge
                }
            }
        }
        return null
    }

}
