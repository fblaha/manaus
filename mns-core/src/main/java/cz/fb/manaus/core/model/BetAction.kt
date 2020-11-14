package cz.fb.manaus.core.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document
data class BetAction(
        @Id
        val id: String,
        val betActionType: BetActionType,
        val version: Int,
        @Indexed
        val time: Instant,
        val price: Price,
        @Indexed
        val marketId: String,
        val selectionId: Long,
        @Indexed
        val betId: String? = null,
        val chargeGrowth: Double? = null,
        val proposers: Set<String> = emptySet(),
        val runnerPrices: List<RunnerPrices>,
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
