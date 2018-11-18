package cz.fb.manaus.core.model

import java.util.logging.Level
import java.util.logging.Logger

private val log = Logger.getLogger(MarketSnapshot::class.java.simpleName)

data class SideSelection(val side: Side, val selectionId: Long)

internal fun getMarketCoverage(bets: List<Bet>): Map<SideSelection, Bet> {
    val sortedBets = bets.sortedBy { it.placedDate }
    val result = mutableMapOf<SideSelection, Bet>()
    for (bet in sortedBets) {
        val side = bet.requestedPrice.side
        val predecessor = result[SideSelection(side, bet.selectionId)]
        if (predecessor != null) {
            log.log(Level.WARNING, "Suspicious relationship between predecessor '$predecessor'' and successor ''$bet''")
        }
        result[SideSelection(side, bet.selectionId)] = bet
    }
    return result
}

data class MarketSnapshot(val runnerPrices: List<RunnerPrices>,
                          val market: Market,
                          val currentBets: List<Bet>,
                          val coverage: Map<SideSelection, Bet>,
                          val tradedVolume: Map<Long, TradedVolume>? = null) {

    companion object {
        fun from(runnerPrices: List<RunnerPrices>, market: Market, currentBets: List<Bet>,
                 tradedVolume: Map<Long, TradedVolume>? = null): MarketSnapshot {
            val coverage = getMarketCoverage(currentBets)
            return MarketSnapshot(runnerPrices, market, currentBets, coverage, tradedVolume)
        }
    }

}
