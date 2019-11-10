package cz.fb.manaus.reactor.betting.action

import cz.fb.manaus.reactor.betting.BetCommand

interface BetCommandHandler {

    fun onBetCommand(command: BetCommand): BetCommand

}
