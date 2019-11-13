package cz.fb.manaus.reactor.betting.listener

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.BetCommand
import cz.fb.manaus.reactor.betting.BetEvent

interface BetEventListener {

    val side: Side

    fun onBetEvent(event: BetEvent): List<BetCommand>

}