package cz.fb.manaus.reactor.betting.listener

import cz.fb.manaus.core.model.MarketSnapshotEvent
import cz.fb.manaus.core.model.SideSelection
import cz.fb.manaus.reactor.betting.BetCommand
import cz.fb.manaus.reactor.price.Fairness
import org.springframework.stereotype.Component

@Component
class BetEventNotifier(
        private val betEventFactory: BetEventFactory,
        private val listeners: List<BetEventListener>
) {

    fun notify(selectionId: Long, fairness: Fairness, snapshotEvent: MarketSnapshotEvent): List<BetCommand> {
        val collector = mutableListOf<BetCommand>()
        for (listener in listeners) {
            val betEvent = betEventFactory.create(
                    sideSelection = SideSelection(listener.side, selectionId),
                    snapshot = snapshotEvent.snapshot,
                    fairness = fairness,
                    account = snapshotEvent.account
            )
            listener.onBetEvent(betEvent)?.let { collector.add(it) }
        }
        return collector.toList()
    }
}