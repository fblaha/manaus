package cz.fb.manaus.reactor.betting.proposer

import cz.fb.manaus.reactor.betting.BetCommand
import cz.fb.manaus.reactor.betting.action.BetCommandHandler
import io.micrometer.core.instrument.Metrics
import org.springframework.stereotype.Component

@Component
class ProposerMetricsUpdater : BetCommandHandler {

    override fun onBetCommand(command: BetCommand): BetCommand {
        val action = command.bet.local
        if (action != null) {
            val side = action.price.side.name.toLowerCase()
            action.proposers.forEach {
                Metrics.counter(
                        "mns_proposer_stats",
                        "side", side,
                        "proposer", it
                ).increment()
            }
        }
        return command
    }

}
