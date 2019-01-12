package cz.fb.manaus.core.model


data class SideSelection(val side: Side, val selectionId: Long)

internal fun getMarketCoverage(bets: List<Bet>): Map<SideSelection, Bet> {
    return bets.groupBy { SideSelection(it.requestedPrice.side, it.selectionId) }
            .mapValues { it.value.maxBy { bet -> bet.placedDate }!! }
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
