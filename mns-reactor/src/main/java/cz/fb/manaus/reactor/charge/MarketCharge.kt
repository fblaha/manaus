package cz.fb.manaus.reactor.charge

import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.SettledBet
import java.lang.Math.max
import java.util.*

data class MarketCharge(private val totalProfit: Double, private val totalPositiveProfit: Double,
                        private val totalCharge: Double, private val profits: Map<String, Double>) {

    fun getChargeContribution(betId: String): Double {
        if (Price.amountEq(totalCharge, 0.0)) return 0.0
        val profit = max(profits[betId]!!, 0.0)
        return Price.round(totalCharge * profit / totalPositiveProfit)
    }

    fun getTotalProfit(): Double {
        return Price.round(totalProfit)
    }

    fun getTotalCharge(): Double {
        return Price.round(totalCharge)
    }

    companion object {

        fun fromBets(chargeRate: Double, bets: Iterable<SettledBet>): MarketCharge {
            val profits = HashMap<String, Double>()
            var totalProfit = 0.0
            var totalPositiveProfit = 0.0
            for (bet in bets) {
                val betId = bet.id
                profits[betId] = bet.profitAndLoss
                totalProfit += bet.profitAndLoss
                totalPositiveProfit += max(bet.profitAndLoss, 0.0)
            }
            val totalCharge = Price.round(chargeRate * max(totalProfit, 0.0))
            return MarketCharge(totalProfit, totalPositiveProfit, totalCharge, profits)
        }
    }
}
