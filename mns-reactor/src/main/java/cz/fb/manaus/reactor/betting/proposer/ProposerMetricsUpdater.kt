package cz.fb.manaus.reactor.betting.proposer

import com.codahale.metrics.MetricRegistry
import cz.fb.manaus.core.model.BetAction
import cz.fb.manaus.reactor.betting.action.BetActionListener
import org.springframework.stereotype.Component

@Component
class ProposerMetricsUpdater(private val metricRegistry: MetricRegistry) : BetActionListener {

    override fun onAction(action: BetAction) {
        val side = action.price.side.name.toLowerCase()
        action.proposers.map { "proposer.$side.$it" }
                .forEach { metricRegistry.counter(it).inc() }
    }
}
