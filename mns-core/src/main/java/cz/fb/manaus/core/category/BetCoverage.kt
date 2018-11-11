package cz.fb.manaus.core.category

import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.model.Side

data class BetCoverage(private val coverage: Map<Pair<String, Long>, List<RealizedBet>>) {

    fun getBets(marketId: String, selectionId: Long, side: Side?): List<RealizedBet> {
        var bets = coverage[Pair(marketId, selectionId)]!!
        if (side != null) {
            bets = bets.filter { it.settledBet.price.side === side }
        }
        return bets
    }

    fun getSides(marketId: String, selectionId: Long): Set<Side> {
        return coverage[Pair(marketId, selectionId)]!!.map { it.settledBet.price.side }.toSet()

    }

    fun isCovered(marketId: String, selectionId: Long): Boolean {
        val sides = getSides(marketId, selectionId)
        return sides.size == 2
    }

    fun getAmount(marketId: String, selectionId: Long, side: Side): Double {
        return coverage[Pair(marketId, selectionId)]!!
                .filter { it.settledBet.price.side === side }.map { it.settledBet.price.amount }.sum()
    }

    fun getPrice(marketId: String, selectionId: Long, side: Side): Double {
        return coverage[Pair(marketId, selectionId)]!!
                .filter { it.settledBet.price.side === side }.map { it.settledBet.price.price }.average()
    }

    companion object {
        val EMPTY = BetCoverage(emptyMap())
        private fun getCoverageKey(bet: RealizedBet): Pair<String, Long> {
            return Pair(bet.market.id, bet.settledBet.selectionId)
        }

        fun from(bets: List<RealizedBet>): BetCoverage {
            return BetCoverage(bets.groupBy { getCoverageKey(it) })
        }
    }

}
