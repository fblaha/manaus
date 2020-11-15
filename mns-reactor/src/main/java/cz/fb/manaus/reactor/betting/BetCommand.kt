package cz.fb.manaus.reactor.betting

import cz.fb.manaus.core.model.BetActionType
import cz.fb.manaus.core.model.CollectedBets
import cz.fb.manaus.core.model.TrackedBet

data class BetCommand(val bet: TrackedBet) {
    val action = bet.local
    val cancel: Boolean = action == null
    val place: Boolean = action != null && action.betActionType == BetActionType.PLACE
    val update: Boolean = action != null && action.betActionType == BetActionType.UPDATE
}

fun toCollectedBets(commands: List<BetCommand>): CollectedBets {
    val (cancel, placeOrUpdate) = commands.partition { it.cancel }
    val (place, update) = placeOrUpdate.partition { it.place }
    return CollectedBets(
            place = place.map { it.bet },
            update = update.map { it.bet },
            cancel = cancel.mapNotNull { it.bet.remote.betId }
    )
}
