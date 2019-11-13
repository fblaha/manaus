package cz.fb.manaus.reactor.betting.listener

import cz.fb.manaus.reactor.betting.BetCommand
import cz.fb.manaus.reactor.betting.BetEvent

interface BetEventListener {

    fun onBetEvent(event: BetEvent): List<BetCommand>

}