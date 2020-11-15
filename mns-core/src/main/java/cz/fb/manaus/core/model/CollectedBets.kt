package cz.fb.manaus.core.model

data class CollectedBets(
        val place: List<TrackedBet>,
        val update: List<TrackedBet>,
        val cancel: List<String>
) {

    val empty: Boolean
        get() = place.isEmpty() && update.isEmpty() && cancel.isEmpty()

    fun minify(): CollectedBets {
        return copy(
                place = place.map { minify(it) },
                update = update.map { minify(it) },
                cancel = cancel
        )
    }
}

fun minify(bet: TrackedBet): TrackedBet {
    val action = bet.local ?: error("no action")
    val priceLess = action.copy(runnerPrices = emptyList())
    return bet.copy(local = priceLess)
}
