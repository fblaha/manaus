package cz.fb.manaus.reactor.betting.action

import cz.fb.manaus.core.repository.BetActionRepository
import cz.fb.manaus.reactor.betting.BetCommand
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile(ManausProfiles.DB)
class ActionPersistHandler(private val betActionRepository: BetActionRepository) : BetCommandHandler {

    override fun onBetCommand(command: BetCommand): BetCommand {
        val (bet, cancel) = command
        val action = bet.local
        return if (cancel) {
            command
        } else {
            val saved = betActionRepository.save(action)
            BetCommand(bet.copy(local = saved))
        }
    }
}