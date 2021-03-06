package cz.fb.manaus.core.batch

import cz.fb.manaus.core.model.Bet
import cz.fb.manaus.core.model.TrackedBet
import cz.fb.manaus.core.repository.BetActionRepository
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.logging.Logger

@Component
@Profile(ManausProfiles.DB)
class ActionLoader(
        private val betActionRepository: BetActionRepository,
) {

    private val log = Logger.getLogger(ActionLoader::class.simpleName)

    fun load(bet: Bet): TrackedBet? {
        val betId = bet.betId ?: error("missing betId")
        return when (val action = betActionRepository.findRecentBetAction(betId)) {
            null -> {
                log.warning { "unknown bet '$betId'" }
                null
            }
            else -> TrackedBet(bet, action)
        }
    }

}
