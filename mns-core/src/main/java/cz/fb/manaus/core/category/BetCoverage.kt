package cz.fb.manaus.core.category

import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.core.model.Side

data class BetCoverage(private val coverage: Map<Pair<String, Long>, List<SettledBet>>) {

    fun getBets(marketId: String, selectionId: Long, side: Side?): List<SettledBet> {
        var bets = coverage[Pair(marketId, selectionId)]!!
        if (side != null) {
            bets = bets.filter { it.price.side === side }
        }
        return bets
    }

    fun getSides(marketId: String, selectionId: Long): Set<Side> {
        return coverage[Pair(marketId, selectionId)]!!.map { it.price.side }.toSet()

    }

    fun isCovered(marketId: String, selectionId: Long): Boolean {
        val sides = getSides(marketId, selectionId)
        return sides.size == 2
    }

    fun getAmount(marketId: String, selectionId: Long, side: Side): Double {
        return coverage[Pair(marketId, selectionId)]!!
                .filter { it.price.side === side }.map { it.price.amount }.sum()
    }

    fun getPrice(marketId: String, selectionId: Long, side: Side): Double {
        return coverage[Pair(marketId, selectionId)]!!
                .filter { it.price.side === side }.map { it.price.price }.average()
    }

    companion object {
        val EMPTY = BetCoverage(emptyMap())
        private fun getCoverageKey(bet: SettledBet): Pair<String, Long> {
            return Pair(bet.betAction.market.id, bet.selectionId)
        }

        fun from(bets: List<SettledBet>): BetCoverage {
            return BetCoverage(bets.groupBy { getCoverageKey(it) })
        }
    }

}
