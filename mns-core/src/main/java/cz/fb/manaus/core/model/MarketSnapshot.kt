package cz.fb.manaus.core.model


data class SideSelection(val side: Side, val selectionId: Long) {
    val oppositeSide: SideSelection
        get() = copy(side = side.opposite)
}

fun getMarketCoverage(bets: List<TrackedBet>): Map<SideSelection, TrackedBet> {
    return bets.groupBy { SideSelection(it.remote.requestedPrice.side, it.remote.selectionId) }
            .mapValues { it.value.maxByOrNull { bet -> bet.remote.placedDate } ?: error("empty") }
}

data class MarketSnapshot(
        val runnerPrices: List<RunnerPrices>,
        val market: Market,
        val currentBets: List<TrackedBet>,
        val tradedVolume: Map<Long, TradedVolume>? = null
) {
    val coverage: Map<SideSelection, TrackedBet> = getMarketCoverage(currentBets)
}

fun Map<SideSelection, TrackedBet>.isActive(selectionId: Long): Boolean =
        Side.values().any { SideSelection(it, selectionId) in this }


data class MarketSnapshotEvent(
        val snapshot: MarketSnapshot,
        val account: Account
)
