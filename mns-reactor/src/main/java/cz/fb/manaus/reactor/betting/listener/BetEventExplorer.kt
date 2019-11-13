package cz.fb.manaus.reactor.betting.listener

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.model.SideSelection
import cz.fb.manaus.core.model.isActive
import cz.fb.manaus.reactor.betting.BetCommand
import cz.fb.manaus.reactor.price.FairnessPolynomialCalculator
import org.springframework.beans.factory.annotation.Autowired

class BetEventExplorer(
        private val side: Side,
        private val betEventListener: BetEventListener
) : MarketSnapshotListener {

    @Autowired
    private lateinit var flowFilterRegistry: FlowFilterRegistry
    @Autowired
    private lateinit var calculator: FairnessPolynomialCalculator
    @Autowired
    private lateinit var betEventFactory: BetEventFactory

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
                    val event = betEventFactory.create(SideSelection(side, selectionId), snapshot, fairness, account)
                    collector.addAll(betEventListener.onBetEvent(event))
                }
            }
        }
        return collector.toList()
    }


}
