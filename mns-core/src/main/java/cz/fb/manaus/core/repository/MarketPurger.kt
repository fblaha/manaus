package cz.fb.manaus.core.repository

import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Instant


@Component
@Profile(ManausProfiles.DB)
class MarketPurger(private val marketRepository: MarketRepository,
                   private val betActionRepository: BetActionRepository) {

    fun purgeInactive(olderThan: Instant): Int {
        val markets = marketRepository.find(to = olderThan)
        var count = 0
        for (market in markets) {
            val actions = betActionRepository.find(market.id)
            if (actions.isEmpty()) {
                marketRepository.delete(market.id)
                count++
            }
        }
        return count
    }
}