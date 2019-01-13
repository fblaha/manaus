package cz.fb.manaus.reactor.betting

import com.google.common.base.Preconditions.checkState
import cz.fb.manaus.core.model.Bet
import cz.fb.manaus.core.model.CollectedBets
import cz.fb.manaus.core.model.Side
import java.util.Objects.requireNonNull

class BetCollector {

    private val toUpdate = mutableListOf<BetCommand>()
    private val toPlace = mutableListOf<BetCommand>()
    private val toCancel = mutableListOf<Bet>()

    val isEmpty: Boolean
        get() = toPlace.isEmpty() && toUpdate.isEmpty() && toCancel.isEmpty()

    fun updateBet(command: BetCommand) {
        requireNonNull(command.bet.betId)
        toUpdate.add(command)
    }

    fun placeBet(betCommand: BetCommand) {
        checkState(betCommand.bet.betId == null)
        toPlace.add(betCommand)
    }

    fun cancelBet(oldBet: Bet) {
        requireNonNull(oldBet.betId)
        toCancel.add(oldBet)
    }


    fun getToPlace(): List<BetCommand> {
        return toPlace.toList()
    }

    fun getToUpdate(): List<BetCommand> {
        return toUpdate.toList()
    }

    private fun getToCancel(): List<Bet> {
        return toCancel.toList()
    }

    fun toCollectedBets(): CollectedBets {
        val bets = CollectedBets.create()
        getToCancel().mapNotNull { it.betId }.forEach { bets.cancel.add(it) }
        getToUpdate().map { it.bet }.forEach { bets.update.add(it) }
        getToPlace().map { it.bet }.forEach { bets.place.add(it) }
        return bets
    }

    fun findBet(marketId: String, selId: Long, side: Side): Bet? {
        val placeOrUpdate = (toPlace + toUpdate).map { it.bet }

        return (placeOrUpdate + toCancel).find {
            it.marketId == marketId
                    && it.selectionId == selId
                    && it.requestedPrice.side === side
        }
    }
}
