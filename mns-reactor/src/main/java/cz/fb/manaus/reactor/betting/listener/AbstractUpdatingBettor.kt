package cz.fb.manaus.reactor.betting.listener

import com.codahale.metrics.MetricRegistry
import cz.fb.manaus.core.model.*
import cz.fb.manaus.reactor.betting.*
import cz.fb.manaus.reactor.betting.listener.ProbabilityComparator.Companion.COMPARATORS
import cz.fb.manaus.reactor.betting.validator.ValidationService
import cz.fb.manaus.reactor.betting.validator.Validator
import cz.fb.manaus.reactor.charge.ChargeGrowthForecaster
import cz.fb.manaus.reactor.price.Fairness
import cz.fb.manaus.reactor.price.FairnessPolynomialCalculator
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant
import java.util.logging.Logger

abstract class AbstractUpdatingBettor(private val side: Side,
                                      private val validators: List<Validator>,
                                      private val priceAdviser: PriceAdviser) : MarketSnapshotListener {
    @Autowired
    private lateinit var validationService: ValidationService
    @Autowired
    private lateinit var flowFilterRegistry: FlowFilterRegistry
    @Autowired
    private lateinit var calculator: FairnessPolynomialCalculator
    @Autowired
    private lateinit var forecaster: ChargeGrowthForecaster
    @Autowired
    private lateinit var metricRegistry: MetricRegistry

    private val log = Logger.getLogger(AbstractUpdatingBettor::class.simpleName)

    override fun onMarketSnapshot(marketSnapshotEvent: MarketSnapshotEvent) {
        val (snapshot, account, collector) = marketSnapshotEvent
        val (marketPrices, market, _, coverage, _) = snapshot
        val flowFilter = flowFilterRegistry.getFlowFilter(market.type!!)
        val fairness = calculator.getFairness(marketPrices)
        val credibleSide = fairness.moreCredibleSide
        if (credibleSide != null) {
            val ordering = COMPARATORS[credibleSide] ?: error("no such side")
            val sortedPrices = marketPrices.sortedWith(ordering)
            check(sortedPrices.map { it.selectionId }.distinct().count() ==
                    sortedPrices.map { it.selectionId }.count())

            val (priceValidators, prePriceValidators) = validators.partition { it.isPriceRequired }

            for ((i, runnerPrices) in sortedPrices.withIndex()) {
                val selectionId = runnerPrices.selectionId
                val runner = market.getRunner(selectionId)
                val sideSelection = SideSelection(side, selectionId)
                val activeSelection = sideSelection in coverage || sideSelection.oppositeSide in coverage
                val accepted = i in flowFilter.indexRange && flowFilter.runnerPredicate(market, runner)
                if (activeSelection || accepted) {
                    val event = buildEvent(selectionId, snapshot, fairness, account, coverage)
                    val oldBet = coverage[sideSelection]
                    val prePriceValidation = validationService.validate(event, prePriceValidators)
                    if (!prePriceValidation.isSuccess) {
                        cancelBet(oldBet, collector)
                        continue
                    }

                    val newPrice = priceAdviser.getNewPrice(event)
                    if (newPrice == null) {
                        cancelBet(oldBet, collector)
                        continue
                    }
                    event.newPrice = newPrice.price
                    event.proposers = newPrice.proposers

                    if (oldBet != null && oldBet.isMatched) continue
                    val priceValidation = validationService.validate(event, priceValidators)
                    if (priceValidation.isSuccess) {
                        bet(event, collector)
                    }
                }
            }
        }
    }

    private fun buildEvent(
            selectionId: Long,
            snapshot: MarketSnapshot,
            fairness: Fairness,
            account: Account, coverage:
            Map<SideSelection, Bet>): BetEvent {

        val forecast = forecaster.getForecast(
                selectionId = selectionId,
                betSide = side,
                snapshot = snapshot,
                fairness = fairness,
                commission = account.provider.commission
        )
        val metrics = BetMetrics(
                chargeGrowthForecast = forecast,
                fairness = fairness,
                actualTradedVolume = snapshot.tradedVolume?.get(key = selectionId)
        )
        return BetEvent(
                market = snapshot.market,
                side = side,
                selectionId = selectionId,
                marketPrices = snapshot.runnerPrices,
                account = account,
                coverage = coverage,
                metrics = metrics
        )
    }

    private fun bet(event: BetEvent, betCollector: BetCollector) {
        val action = event.betAction
        val newPrice = event.newPrice!!

        val oldBet = event.oldBet
        if (oldBet != null) {
            betCollector.updateBet(BetCommand(oldBet replacePrice newPrice.price, action))
        } else {
            val market = event.market
            val bet = Bet(marketId = market.id,
                    placedDate = Instant.now(),
                    selectionId = event.runnerPrices.selectionId,
                    requestedPrice = newPrice)
            betCollector.placeBet(BetCommand(bet, action))
        }
        log.info { "bet ${action.betActionType} action '$action'" }
    }

    private fun cancelBet(oldBet: Bet?, betCollector: BetCollector) {
        if (oldBet != null && !oldBet.isMatched) {
            metricRegistry.counter("bet.cancel").inc()
            betCollector.cancelBet(oldBet)
            log.info { "bet cancel - unable propose price for bet '$oldBet'" }
        }
    }
}
