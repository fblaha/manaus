package cz.fb.manaus.reactor.charge

import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.core.model.amountEq
import kotlin.math.max

data class MarketCharge(private val totalProfit: Double,
                        private val totalPositiveProfit: Double,
                        private val totalCharge: Double,
                        private val profits: Map<String, Double>) {

    fun getChargeContribution(betId: String): Double {
        if (totalCharge amountEq 0.0) return 0.0
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
            val profits = bets.associate { it.id to it.profitAndLoss }.toMap()
            val totalProfit = bets.sumByDouble { it.profitAndLoss }
            val totalPositiveProfit = bets.map { it.profitAndLoss }.filter { it > 0 }.sum()
            val totalCharge = Price.round(chargeRate * max(totalProfit, 0.0))
            return MarketCharge(totalProfit, totalPositiveProfit, totalCharge, profits)
        }
    }
}
