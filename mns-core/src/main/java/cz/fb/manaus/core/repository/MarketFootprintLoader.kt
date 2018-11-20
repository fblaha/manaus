package cz.fb.manaus.core.repository

import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.model.MarketFootprint
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile(ManausProfiles.DB)
class MarketFootprintLoader(private val betActionRepository: BetActionRepository,
                            private val settledBetRepository: SettledBetRepository) {

    fun toFootprint(market: Market): MarketFootprint {
        val betActions = betActionRepository.find(market.id)
        val betIDs = betActions.mapNotNull { it.betId }.toSet()
        val bets = betIDs.mapNotNull { settledBetRepository.read(it) }
        return MarketFootprint(market, betActions, bets)
    }
}
