package cz.fb.manaus.reactor.betting.status

import cz.fb.manaus.core.model.MarketStatus
import cz.fb.manaus.core.repository.Repository
import cz.fb.manaus.reactor.betting.BetCommand
import cz.fb.manaus.reactor.betting.action.BetCommandHandler
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile(ManausProfiles.DB)
class MarketStatusHandler(
        private val repository: Repository<MarketStatus>
) : BetCommandHandler {

    override fun onBetCommand(command: BetCommand): BetCommand {
        val (bet, _) = command
        val status = repository.read(bet.marketId) ?: error("no such status")
        val bets = status.bets
        val newBets = when {
            command.isCancel -> bets.filter { it.betId != bet.betId }
            command.isPlace -> bets + listOf(bet)
            command.isUpdate -> bets.filter { it.betId != bet.betId }  + listOf(bet)
            else -> error("invalid command")
        }
        repository.save(status.copy(bets = newBets))
        return command
    }
}