package cz.fb.manaus.core.model

data class RealizedBet(val settledBet: SettledBet, val betAction: BetAction, val market: Market) {
    fun replacePrice(price: Price): RealizedBet {
        return this.copy(
                settledBet = this.settledBet.copy(price = price),
                betAction = this.betAction.copy(price = price)
        )
    }
}
