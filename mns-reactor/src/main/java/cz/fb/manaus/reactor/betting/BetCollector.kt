package cz.fb.manaus.reactor.betting

import cz.fb.manaus.core.model.Bet
import cz.fb.manaus.core.model.CollectedBets

class BetCollector {

    private val update = mutableListOf<BetCommand>()
    private val place = mutableListOf<BetCommand>()
    private val cancel = mutableListOf<Bet>()

    val empty: Boolean
        get() = place.isEmpty() && update.isEmpty() && cancel.isEmpty()

    fun updateBet(command: BetCommand) {
        requireNotNull(command.bet.betId)
        update.add(command)
    }

    fun placeBet(betCommand: BetCommand) {
        check(betCommand.bet.betId == null)
        place.add(betCommand)
    }

    fun cancelBet(bet: Bet) {
        requireNotNull(bet.betId)
        cancel.add(bet)
    }


    val placeCommands: List<BetCommand>
        get() {
            return place.toList()
        }

    val updateCommands: List<BetCommand>
        get() {
            return update.toList()
        }

    val cancelCommands: List<Bet>
        get() {
            return cancel.toList()
        }

    fun toCollectedBets(): CollectedBets {
        return CollectedBets(
                placeCommands.map { it.bet },
                updateCommands.map { it.bet },
                cancelCommands.mapNotNull { it.betId }
        )
    }

}
