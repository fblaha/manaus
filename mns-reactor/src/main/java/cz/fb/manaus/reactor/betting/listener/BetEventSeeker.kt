package cz.fb.manaus.reactor.betting.listener

import cz.fb.manaus.core.model.SideSelection
import cz.fb.manaus.core.model.isActive
import cz.fb.manaus.reactor.betting.BetCommand
import cz.fb.manaus.reactor.price.FairnessPolynomialCalculator
import org.springframework.stereotype.Component

@Component
class BetEventSeeker(
        private val flowFilterRegistry: FlowFilterRegistry,
        private val calculator: FairnessPolynomialCalculator,
        private val betEventFactory: BetEventFactory,
        private val betEventListeners: List<BetEventListener>
) : MarketSnapshotListener {

    override fun onMarketSnapshot(marketSnapshotEvent: MarketSnapshotEvent): List<BetCommand> {
        val (snapshot, account) = marketSnapshotEvent
        val (marketPrices, market) = snapshot
        val flowFilter = flowFilterRegistry.getFlowFilter(market.type!!)
        val fairness = calculator.getFairness(marketPrices)
        val credibleSide = fairness.moreCredibleSide
        val collector = mutableListOf<BetCommand>()
        if (credibleSide != null) {
            val sortedPrices = sortPrices(credibleSide, marketPrices)
            for ((i, runnerPrices) in sortedPrices.withIndex()) {
                val selectionId = runnerPrices.selectionId
                val runner = market.getRunner(selectionId)
                val accepted = i in flowFilter.indexRange && flowFilter.runnerPredicate(market, runner)
                if (snapshot.coverage.isActive(selectionId) || accepted) {
                    for (betEventListener in betEventListeners) {
                        val event = betEventFactory.create(
                                sideSelection = SideSelection(betEventListener.side, selectionId),
                                snapshot = snapshot,
                                fairness = fairness,
                                account = account
                        )
                        betEventListener.onBetEvent(event)?.let { collector.add(it) }
                    }
                }
            }
        }
        return collector.toList()
    }


}
