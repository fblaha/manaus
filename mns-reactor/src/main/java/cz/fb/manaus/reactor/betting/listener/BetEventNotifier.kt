package cz.fb.manaus.reactor.betting.listener

import cz.fb.manaus.reactor.betting.BetCommand
import cz.fb.manaus.reactor.betting.BetEvent
import org.springframework.stereotype.Component

@Component
class BetEventNotifier(
        private val listeners: List<BetEventListener>
) {

    fun notify(betEvent: BetEvent): List<BetCommand> {
        val collector = mutableListOf<BetCommand>()
        for (listener in listeners) {
            if (listener.side == betEvent.side) {
                listener.onBetEvent(betEvent)?.let { collector.add(it) }
            }
        }
        return collector.toList()
    }

}