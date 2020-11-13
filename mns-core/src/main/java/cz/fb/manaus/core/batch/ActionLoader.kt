package cz.fb.manaus.core.batch

import cz.fb.manaus.core.model.Bet
import cz.fb.manaus.core.repository.BetActionRepository
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile(ManausProfiles.DB)
class ActionLoader(
        private val betActionRepository: BetActionRepository,
) {

    fun load(bet: Bet): Bet {
        val betId = bet.betId ?: error("missing betId")
        return bet.copy(action = betActionRepository.findRecentBetAction(betId))
    }

}
