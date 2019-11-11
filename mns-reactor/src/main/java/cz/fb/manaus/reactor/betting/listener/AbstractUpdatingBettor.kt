package cz.fb.manaus.reactor.betting.listener

import cz.fb.manaus.core.model.Bet
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.model.SideSelection
import cz.fb.manaus.core.model.isActive
import cz.fb.manaus.reactor.betting.BetCommand
import cz.fb.manaus.reactor.betting.PriceAdviser
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.ValidationService
import cz.fb.manaus.reactor.betting.validator.Validator
import cz.fb.manaus.reactor.price.FairnessPolynomialCalculator
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractUpdatingBettor(
        private val side: Side,
        validators: List<Validator>,
        private val priceAdviser: PriceAdviser
) : MarketSnapshotListener {

    private val validators = validators.partition { it is PriceProposer }

    @Autowired
    private lateinit var validationService: ValidationService
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
        val coverage = snapshot.coverage
        val flowFilter = flowFilterRegistry.getFlowFilter(market.type!!)
        val fairness = calculator.getFairness(marketPrices)
        val credibleSide = fairness.moreCredibleSide
        val collector = mutableListOf<BetCommand>()
        if (credibleSide != null) {
            val sortedPrices = sortPrices(credibleSide, marketPrices)
            val (prePriceValidators, priceValidators) = validators
            for ((i, runnerPrices) in sortedPrices.withIndex()) {
                val selectionId = runnerPrices.selectionId
                val runner = market.getRunner(selectionId)
                val sideSelection = SideSelection(side, selectionId)
                val accepted = i in flowFilter.indexRange && flowFilter.runnerPredicate(market, runner)
                if (coverage.isActive(selectionId) || accepted) {
                    val event = betEventFactory.create(sideSelection, snapshot, fairness, account)
                    val oldBet = coverage[sideSelection]
                    val prePriceValidation = validationService.validate(event, prePriceValidators)
                    cancelOnDrop(prePriceValidation, oldBet, collector)
                    if (prePriceValidation == ValidationResult.OK) {
                        val newPrice = priceAdviser.getNewPrice(event)
                        if (newPrice == null) {
                            betCommandIssuer.tryCancel(oldBet)?.let { collector.add(it) }
                            continue
                        }
                        event.newPrice = newPrice.price
                        event.proposers = newPrice.proposers

                        if (oldBet != null && oldBet.isMatched) continue
                        val priceValidation = validationService.validate(event, priceValidators)
                        cancelOnDrop(priceValidation, oldBet, collector)
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
