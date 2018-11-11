package cz.fb.manaus.core.model

import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import java.util.logging.Level
import java.util.logging.Logger

private val log = Logger.getLogger(MarketSnapshot::class.java.simpleName)

internal fun getMarketCoverage(bets: List<Bet>): Table<Side, Long, Bet> {
    val sortedBets = bets.sortedBy { it.placedDate }
    val result = HashBasedTable.create<Side, Long, Bet>()
    for (bet in sortedBets) {
        val side = bet.requestedPrice.side
        val predecessor = result.get(side, bet.selectionId)
        if (predecessor != null) {
            log.log(Level.WARNING, "Suspicious relationship between predecessor '$predecessor'' and successor ''$bet''")
        }
        result.put(side, bet.selectionId, bet)
    }
    return result
}

data class MarketSnapshot(val runnerPrices: List<RunnerPrices>,
                          val market: Market,
                          val currentBets: List<Bet>,
                          val coverage: Table<Side, Long, Bet>,
                          val tradedVolume: Map<Long, TradedVolume>? = null) {

    companion object {
        fun from(runnerPrices: List<RunnerPrices>, market: Market, currentBets: List<Bet>,
                 tradedVolume: Map<Long, TradedVolume>? = null): MarketSnapshot {
            val coverage = getMarketCoverage(currentBets)
            return MarketSnapshot(runnerPrices, market, currentBets, coverage, tradedVolume)
        }
    }

}
