package cz.fb.manaus.core.model


data class SideSelection(val side: Side, val selectionId: Long) {
    val oppositeSide: SideSelection
        get() {
            return copy(side = side.opposite)
        }
}

fun getMarketCoverage(bets: List<Bet>): Map<SideSelection, Bet> {
    return bets.groupBy { SideSelection(it.requestedPrice.side, it.selectionId) }
            .mapValues { it.value.maxBy { bet -> bet.placedDate }!! }
}

data class MarketSnapshot(
        val runnerPrices: List<RunnerPrices>,
        val market: Market,
        val currentBets: List<Bet>,
        val tradedVolume: Map<Long, TradedVolume>? = null
) {
    val coverage: Map<SideSelection, Bet> = getMarketCoverage(currentBets)
}

fun Map<SideSelection, Bet>.isActive(selectionId: Long): Boolean = Side.values().any { SideSelection(it, selectionId) in this }


data class MarketSnapshotEvent(
        val snapshot: MarketSnapshot,
        val account: Account
)
