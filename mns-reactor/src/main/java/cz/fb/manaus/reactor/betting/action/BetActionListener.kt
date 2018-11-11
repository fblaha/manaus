package cz.fb.manaus.reactor.betting.action

import cz.fb.manaus.core.model.BetAction

interface BetActionListener {

    fun onAction(action: BetAction)

}
