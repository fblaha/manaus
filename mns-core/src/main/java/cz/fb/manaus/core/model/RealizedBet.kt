package cz.fb.manaus.core.model

data class RealizedBet(val settledBet: SettledBet, val betAction: BetAction, val market: Market)
