package cz.fb.manaus.reactor.betting.proposer

import com.codahale.metrics.MetricRegistry
import com.google.common.base.Joiner
import cz.fb.manaus.core.model.BetAction
import cz.fb.manaus.reactor.betting.action.BetActionListener
import cz.fb.manaus.reactor.betting.action.BetUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ProposerMetricsUpdater : BetActionListener {
    @Autowired
    private lateinit var betUtils: BetUtils
    @Autowired
    private lateinit var metricRegistry: MetricRegistry

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
