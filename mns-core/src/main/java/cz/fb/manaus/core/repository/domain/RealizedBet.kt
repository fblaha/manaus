package cz.fb.manaus.core.repository.domain

data class RealizedBet(val settledBet: SettledBet, val betAction: BetAction, val market: Market)
