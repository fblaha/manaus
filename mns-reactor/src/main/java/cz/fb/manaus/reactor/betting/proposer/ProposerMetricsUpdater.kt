package cz.fb.manaus.reactor.betting.proposer

import com.codahale.metrics.MetricRegistry
import com.google.common.base.Joiner
import cz.fb.manaus.core.model.BetAction
import cz.fb.manaus.reactor.betting.action.BetActionListener
import cz.fb.manaus.reactor.betting.action.BetUtils
import org.springframework.stereotype.Component

@Component
class ProposerMetricsUpdater(private val betUtils: BetUtils,
                             private val metricRegistry: MetricRegistry) : BetActionListener {

    override fun onAction(action: BetAction) {
        val proposers = action.properties[BetAction.PROPOSER_PROP]!!
        val side = action.price.side.name.toLowerCase()
        for (proposer in betUtils.parseProposers(proposers)) {
            val key = Joiner.on('.').join(PROPOSER_METRIC, side, proposer)
            metricRegistry.counter(key).inc()
        }
    }

    companion object {
        const val PROPOSER_METRIC = "proposer"
    }
}
