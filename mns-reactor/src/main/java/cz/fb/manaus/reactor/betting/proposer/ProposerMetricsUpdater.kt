package cz.fb.manaus.reactor.betting.proposer

import cz.fb.manaus.reactor.betting.BetCommand
import cz.fb.manaus.reactor.betting.action.BetCommandHandler
import org.springframework.stereotype.Component

@Component
class ProposerMetricsUpdater : BetCommandHandler {

    // TODO micrometer
    override fun onBetCommand(command: BetCommand): BetCommand {
//        val (_, action) = command
//        if (action != null) {
//            val side = action.price.side.name.toLowerCase()
//            action.proposers.map { "proposer.$side.$it" }
//                    .forEach { metricRegistry.counter(it).inc() }
//        }
        return command
    }

}
