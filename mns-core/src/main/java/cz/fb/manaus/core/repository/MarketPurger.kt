package cz.fb.manaus.core.repository

import cz.fb.manaus.core.model.MarketFootprint
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component


@Component
@Profile(ManausProfiles.DB)
class MarketPurger(private val marketRepository: MarketRepository,
                   private val settledBetRepository: SettledBetRepository,
                   private val betActionRepository: BetActionRepository) {

    fun purge(footprint: MarketFootprint) {
        val (market, _, settledBets) = footprint
        settledBets.forEach { settledBetRepository.delete(it.id) }
        betActionRepository.delete(market.id)
        marketRepository.delete(market.id)
    }
}