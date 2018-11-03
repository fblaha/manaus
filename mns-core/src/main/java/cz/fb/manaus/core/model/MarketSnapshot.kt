package cz.fb.manaus.core.model

import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import java.util.logging.Level
import java.util.logging.Logger

data class MarketSnapshot(val marketPrices: MarketPrices, val currentBets: List<Bet>,
                          val coverage: Table<Side, Long, Bet>,
                          val tradedVolume: Map<Long, TradedVolume>?) {

    companion object {

        private val log = Logger.getLogger(MarketSnapshot::class.java.simpleName)

        internal fun getMarketCoverage(bets: List<Bet>): Table<Side, Long, Bet> {

            val sortedBets = bets.sortedBy { it.placedDate }

            val result = HashBasedTable.create<Side, Long, Bet>()
            for (bet in sortedBets) {
                val side = bet.requestedPrice.side
                val predecessor = result.get(side, bet.selectionId)
                if (predecessor != null) {
                    log.log(Level.WARNING, "Suspicious relationship between predecessor ''{0}'' and successor ''{1}''",
                            arrayOf<Any>(predecessor, bet))

                }
                result.put(side, bet.selectionId, bet)
            }
            return result
        }

        fun from(marketPrices: MarketPrices, currentBets: List<Bet>,
                 tradedVolume: Map<Long, TradedVolume>?): MarketSnapshot {
            val coverage = getMarketCoverage(currentBets)
            return MarketSnapshot(marketPrices, currentBets, coverage, tradedVolume)
        }
    }

}
