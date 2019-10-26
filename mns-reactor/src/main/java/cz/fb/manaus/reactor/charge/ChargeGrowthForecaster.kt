package cz.fb.manaus.reactor.charge

import cz.fb.manaus.core.model.Bet
import cz.fb.manaus.core.model.MarketSnapshot
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.AmountAdviser
import cz.fb.manaus.reactor.price.Fairness
import cz.fb.manaus.reactor.price.ProbabilityCalculator
import cz.fb.manaus.reactor.price.getRunnerPrices
import org.springframework.stereotype.Component

@Component
class ChargeGrowthForecaster(
        private val simulator: MarketChargeSimulator,
        private val probabilityCalculator: ProbabilityCalculator,
        private val amountAdviser: AmountAdviser) {

    private fun convertBetData(currentBets: List<Bet>): Map<Long, List<Price>> {
        return currentBets.groupBy({ it.selectionId }, {
            val price = it.requestedPrice.price
            val matchedAmount = it.matchedAmount
            val side = it.requestedPrice.side
            Price(price, matchedAmount, side)
        })
    }

    fun getForecast(selectionId: Long,
                    betSide: Side,
                    snapshot: MarketSnapshot,
                    fairness: Fairness,
                    commission: Double): Double? {

        val fairnessSide = fairness.moreCredibleSide
        if (fairnessSide != null) {
            val sideFairness = fairness[fairnessSide]!!
            val probabilities = probabilityCalculator.fromFairness(
                    sideFairness, fairnessSide, snapshot.runnerPrices)
            val marketPrices = snapshot.runnerPrices
            val bets = convertBetData(snapshot.currentBets).toMutableMap()
            val runnerPrices = getRunnerPrices(marketPrices, selectionId)
            val oldCharge = simulator.getChargeMean(
                    winnerCount = 1,
                    commission = commission,
                    probabilities = probabilities,
                    bets = bets
            )
            val bestPrice = runnerPrices.getHomogeneous(betSide.opposite).bestPrice
            if (bestPrice != null) {
                val price = bestPrice.price
                val amount = amountAdviser.amount
                val selBets = bets[selectionId] ?: emptyList()
                bets[selectionId] = selBets + listOf(Price(price, amount, betSide))
                val newCharge = simulator.getChargeMean(
                        winnerCount = 1,
                        commission = commission,
                        probabilities = probabilities,
                        bets = bets
                )
                return newCharge / oldCharge
            }
        }
        return null
    }

}
