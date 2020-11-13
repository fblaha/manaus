package cz.fb.manaus.core.manager.filter

import cz.fb.manaus.core.model.MarketSnapshotEvent
import org.springframework.stereotype.Component
import java.util.logging.Logger

@Component
class UnknownBetsFilter : MarketSnapshotEventFilter {

    private val log = Logger.getLogger(UnknownBetsFilter::class.simpleName)

    override fun accept(event: MarketSnapshotEvent): Boolean {
        return event.snapshot.currentBets.filter { it.action == null }
                .onEach { log.warning { "unknown bet '${it.betId}'" } }
                .isEmpty()
    }
}
