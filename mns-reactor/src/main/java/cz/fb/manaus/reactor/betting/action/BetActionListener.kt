package cz.fb.manaus.reactor.betting.action

import cz.fb.manaus.core.repository.domain.BetAction

interface BetActionListener {

    fun onAction(action: BetAction)

}
