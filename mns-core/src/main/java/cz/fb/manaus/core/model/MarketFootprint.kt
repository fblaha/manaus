package cz.fb.manaus.core.model

data class MarketFootprint(
        val market: Market,
        val betActions: List<BetAction>,
        val settledBets: List<SettledBet>
)
