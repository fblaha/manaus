package cz.fb.manaus.reactor.betting.listener

import cz.fb.manaus.core.model.MarketSnapshotEvent
import cz.fb.manaus.core.model.SideSelection
import cz.fb.manaus.core.model.isActive
import cz.fb.manaus.reactor.betting.BetCommand
import cz.fb.manaus.reactor.price.FairnessPolynomialCalculator
import org.springframework.stereotype.Component
import java.util.logging.Logger

@Component
class BetEventSeeker(
        private val flowFilterRegistry: FlowFilterRegistry,
        private val calculator: FairnessPolynomialCalculator,
        private val betEventNotifier: BetEventNotifier,
        private val betEventFactory: BetEventFactory
) : MarketSnapshotListener {

    private val log = Logger.getLogger(BetEventSeeker::class.simpleName)

    override fun onMarketSnapshot(marketSnapshotEvent: MarketSnapshotEvent): List<BetCommand> {
        val (snapshot, account) = marketSnapshotEvent
        val (marketPrices, market) = snapshot
        val flowFilter = flowFilterRegistry.getFlowFilter(market.type!!)
        val collector = mutableListOf<BetCommand>()
        val fairness = calculator.getFairness(marketPrices)
        val credibleSide = fairness.moreCredibleSide
        val sortedPrices = if (flowFilter.checkIndex && credibleSide != null) {
            sortPrices(credibleSide, marketPrices)
        } else {
            null
        }
        for ((i, runnerPrices) in (sortedPrices ?: marketPrices).withIndex()) {
            val selectionId = runnerPrices.selectionId
            val runner = market.getRunner(selectionId)
            val acceptIndex = !flowFilter.checkIndex || (sortedPrices != null && flowFilter.acceptIndex(i))
            val accepted = acceptIndex && flowFilter.runnerPredicate(market, runner)
            if (snapshot.coverage.isActive(selectionId) || accepted) {
                for (side in betEventNotifier.activeSides) {
                    val betEvent = betEventFactory.create(
                            sideSelection = SideSelection(side, selectionId),
                            snapshot = snapshot,
                            fairness = fairness,
                            account = account
                    )
                    log.info { "bet event ${market.event.name} - ${runner.name} ($side)" }
                    collector.addAll(betEventNotifier.notify(betEvent))
                }
            }
        }
        return collector.toList()
    }
}
