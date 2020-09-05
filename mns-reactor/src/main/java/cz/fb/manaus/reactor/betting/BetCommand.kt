package cz.fb.manaus.reactor.betting

import cz.fb.manaus.core.model.Bet
import cz.fb.manaus.core.model.BetAction
import cz.fb.manaus.core.model.BetActionType
import cz.fb.manaus.core.model.CollectedBets

data class BetCommand(val bet: Bet, val action: BetAction?) {
    val isCancel: Boolean = action == null
    val isPlace: Boolean = action != null && action.betActionType == BetActionType.PLACE
    val isUpdate: Boolean = action != null && action.betActionType == BetActionType.UPDATE
}

fun toCollectedBets(commands: List<BetCommand>): CollectedBets {
    val (cancel, placeOrUpdate) = commands.partition { it.isCancel }
    val (place, update) = placeOrUpdate.partition { it.isPlace }
    return CollectedBets(
            place = place.map { it.bet },
            update = update.map { it.bet },
            cancel = cancel.mapNotNull { it.bet.betId }
    )
}
