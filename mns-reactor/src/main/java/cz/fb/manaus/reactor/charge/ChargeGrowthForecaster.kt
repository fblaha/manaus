package cz.fb.manaus.reactor.charge

import cz.fb.manaus.core.model.MarketSnapshot
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.SideSelection
import cz.fb.manaus.core.model.TrackedBet
import cz.fb.manaus.reactor.betting.AmountAdviser
import cz.fb.manaus.reactor.price.Fairness
import cz.fb.manaus.reactor.price.ProbabilityCalculator
import cz.fb.manaus.reactor.price.getRunnerPrices
import org.springframework.stereotype.Component
import kotlin.math.min

@Component
class ChargeGrowthForecaster(
        private val simulator: MarketChargeSimulator,
        private val amountAdviser: AmountAdviser
) {

    private fun convertBetData(currentBets: List<TrackedBet>): Map<Long, List<Price>> {
        return currentBets.groupBy({ it.remote.selectionId }, {
            val bet = it.remote
            val price = bet.requestedPrice.price
            val matchedAmount = bet.matchedAmount
            val side = bet.requestedPrice.side
            Price(price, matchedAmount, side)
        })
    }

    fun getForecast(
            sideSelection: SideSelection,
            snapshot: MarketSnapshot,
            fairness: Fairness,
            commission: Double
    ): Double? {
        val (side, selectionId) = sideSelection
        val fairnessSide = fairness.moreCredibleSide
        if (fairnessSide != null) {
            val sideFairness = fairness[fairnessSide]!!
            val probabilities = ProbabilityCalculator.fromFairness(
                    sideFairness, fairnessSide, snapshot.runnerPrices
            )
            val marketPrices = snapshot.runnerPrices
            val bets = convertBetData(snapshot.currentBets).toMutableMap()
            val runnerPrices = getRunnerPrices(marketPrices, selectionId)
            val oldCharge = simulator.getChargeMean(
                    winnerCount = 1,
                    commission = commission,
                    probabilities = probabilities,
                    bets = bets
            )
            val bestPrice = runnerPrices.getHomogeneous(side.opposite).bestPrice
            if (bestPrice != null) {
                val price = bestPrice.price
                val amount = amountAdviser.amount
                val selBets = bets[selectionId] ?: emptyList()
                bets[selectionId] = selBets + listOf(Price(price, amount, side))
                val newCharge = simulator.getChargeMean(
                        winnerCount = 1,
                        commission = commission,
                        probabilities = probabilities,
                        bets = bets
                )
                return min(1000.0, newCharge / oldCharge)
            }
        }
        return null
    }

}
