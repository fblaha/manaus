package cz.fb.manaus.reactor.betting

import cz.fb.manaus.core.model.Bet
import cz.fb.manaus.core.model.BetAction

data class BetCommand(var bet: Bet, var action: BetAction)


