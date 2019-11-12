package cz.fb.manaus.reactor.betting.listener

import cz.fb.manaus.core.model.Bet
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.model.SideSelection
import cz.fb.manaus.core.model.isActive
import cz.fb.manaus.reactor.betting.BetCommand
import cz.fb.manaus.reactor.betting.PriceAdviser
import cz.fb.manaus.reactor.betting.validator.ValidationCoordinator
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.price.FairnessPolynomialCalculator
import org.springframework.beans.factory.annotation.Autowired

class BetCoordinator(
        private val side: Side,
        private val validationCoordinator: ValidationCoordinator,
        private val priceAdviser: PriceAdviser
) : MarketSnapshotListener {

    @Autowired
    private lateinit var flowFilterRegistry: FlowFilterRegistry
    @Autowired
    private lateinit var calculator: FairnessPolynomialCalculator
    @Autowired
    private lateinit var betCommandIssuer: BetCommandIssuer
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
                val sideSelection = SideSelection(side, selectionId)
                val accepted = i in flowFilter.indexRange && flowFilter.runnerPredicate(market, runner)
                if (snapshot.coverage.isActive(selectionId) || accepted) {
                    val event = betEventFactory.create(sideSelection, snapshot, fairness, account)
                    val prePriceValidation = validationCoordinator.validatePrePrice(event)
                    cancelOnDrop(prePriceValidation, event.oldBet, collector)
                    if (prePriceValidation == ValidationResult.OK) {
                        val newPrice = priceAdviser.getNewPrice(event)
                        if (newPrice == null) {
                            betCommandIssuer.tryCancel(event.oldBet)?.let { collector.add(it) }
                            continue
                        }
                        event.newPrice = newPrice.price
                        event.proposers = newPrice.proposers

                        if (event.isOldMatched) continue
                        val priceValidation = validationCoordinator.validatePrice(event)
                        cancelOnDrop(priceValidation, event.oldBet, collector)
                        if (priceValidation == ValidationResult.OK) {
                            check(prePriceValidation == ValidationResult.OK && priceValidation == ValidationResult.OK)
                            collector.add(betCommandIssuer.placeOrUpdate(event))
                        }
                    }
                }
            }
        }
        return collector.toList()
    }

    private fun cancelOnDrop(prePriceValidation: ValidationResult, oldBet: Bet?, collector: MutableList<BetCommand>) {
        if (prePriceValidation == ValidationResult.DROP) {
            betCommandIssuer.tryCancel(oldBet)?.let { collector.add(it) }
        }
    }
}
