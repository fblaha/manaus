package cz.fb.manaus.reactor.betting.listener

import cz.fb.manaus.core.model.MarketSnapshotEvent
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.model.SideSelection
import cz.fb.manaus.core.model.isActive
import cz.fb.manaus.reactor.betting.BetCommand
import cz.fb.manaus.reactor.price.FairnessPolynomialCalculator
import org.springframework.stereotype.Component

@Component
class BetEventSeeker(
        private val flowFilterRegistry: FlowFilterRegistry,
        private val calculator: FairnessPolynomialCalculator,
        private val betEventNotifier: BetEventNotifier,
        private val betEventFactory: BetEventFactory
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
                    for (side in Side.values()) {
                        val betEvent = betEventFactory.create(
                                sideSelection = SideSelection(side, selectionId),
                                snapshot = snapshot,
                                fairness = fairness,
                                account = account
                        )
                        collector.addAll(betEventNotifier.notify(betEvent))
                    }
                }
            }
        }
        return collector.toList()
    }
}
