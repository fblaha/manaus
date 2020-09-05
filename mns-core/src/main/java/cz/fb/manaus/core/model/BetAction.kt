package cz.fb.manaus.core.model

import org.dizitart.no2.IndexType
import org.dizitart.no2.objects.Id
import org.dizitart.no2.objects.Index
import org.dizitart.no2.objects.Indices
import java.time.Instant

@Indices(
        Index(value = "marketId", type = IndexType.NonUnique),
        Index(value = "betId", type = IndexType.NonUnique)
)
data class BetAction(
        @Id val id: Long,
        val betActionType: BetActionType,
        val time: Instant,
        val price: Price,
        val marketId: String,
        val selectionId: Long,
        val betId: String? = null,
        val runnerPrices: List<RunnerPrices>,
        val chargeGrowth: Double? = null,
        val proposers: Set<String> = emptySet()
)


fun getCurrentActions(betActions: List<BetAction>): List<BetAction> {
    validate(betActions)
    val updates = betActions.takeLastWhile { it.betActionType == BetActionType.UPDATE }
    return when (val place = betActions.getOrNull(betActions.size - updates.size - 1)) {
        null -> updates
        else -> listOf(place) + updates
    }
}

private fun validate(betActions: List<BetAction>) {
    val nextZip = betActions.zipWithNext()
    nextZip.forEach { check(it.first.time < it.second.time) }
    nextZip.forEach { check(it.first.selectionId == it.second.selectionId) }
    nextZip.forEach { check(it.first.price.side == it.second.price.side) }
}
