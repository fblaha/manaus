package cz.fb.manaus.reactor.betting.listener

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.BetCommand
import cz.fb.manaus.reactor.betting.BetEvent
import org.springframework.stereotype.Component

@Component
class BetEventNotifier(
        private val listeners: List<BetEventListener>
) {

    val activeSides: Set<Side>
        get() {
            return listeners.map { it.side }.toSet()
        }

    fun notify(betEvent: BetEvent): List<BetCommand> {
        return listeners
                .filter { it.side == betEvent.side }
                .mapNotNull { it.onBetEvent(betEvent) }
    }

}