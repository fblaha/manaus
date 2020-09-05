package cz.fb.manaus.core.model

data class CollectedBets(
        val place: List<Bet>,
        val update: List<Bet>,
        val cancel: List<String>
)

val CollectedBets.empty: Boolean
    get() = place.isEmpty() && update.isEmpty() && cancel.isEmpty()

