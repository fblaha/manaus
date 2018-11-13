package cz.fb.manaus.core.repository

import cz.fb.manaus.spring.ManausProfiles
import org.dizitart.no2.objects.filters.ObjectFilters
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile(ManausProfiles.DB)
class DatabaseCleaner(private val marketRepository: MarketRepository,
                      private val betActionRepository: BetActionRepository,
                      private val settledBetRepository: SettledBetRepository) {

    fun clean() {
        marketRepository.repository.remove(ObjectFilters.ALL)
        betActionRepository.repository.remove(ObjectFilters.ALL)
        settledBetRepository.repository.remove(ObjectFilters.ALL)
    }
}

