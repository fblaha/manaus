package cz.fb.manaus.reactor.betting.listener

import com.codahale.metrics.MetricRegistry
import com.google.common.base.Preconditions.checkState
import cz.fb.manaus.core.model.*
import cz.fb.manaus.reactor.betting.*
import cz.fb.manaus.reactor.betting.listener.ProbabilityComparator.Companion.COMPARATORS
import cz.fb.manaus.reactor.betting.validator.ValidationService
import cz.fb.manaus.reactor.betting.validator.Validator
import cz.fb.manaus.reactor.price.FairnessPolynomialCalculator
import org.apache.commons.math3.util.Precision
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant
import java.util.logging.Level
import java.util.logging.Logger

abstract class AbstractUpdatingBettor(private val side: Side, private val validators: List<Validator>, private val priceAdviser: PriceAdviser) : MarketSnapshotListener {
    @Autowired
    private lateinit var validationService: ValidationService
    @Autowired
    private lateinit var flowFilterRegistry: FlowFilterRegistry
    @Autowired
    private lateinit var calculator: FairnessPolynomialCalculator
    @Autowired
    private lateinit var contextFactory: BetContextFactory
    @Autowired
    private lateinit var metricRegistry: MetricRegistry

    override fun onMarketSnapshot(snapshot: MarketSnapshot, betCollector: BetCollector,
                                  accountMoney: AccountMoney?, categoryBlacklist: Set<String>) {
        val (marketPrices, market, _, coverage, _) = snapshot
        val flowFilter = flowFilterRegistry.getFlowFilter(market.type!!)
        val fairness = calculator.getFairness(marketPrices)
        val credibleSide = fairness.moreCredibleSide!!
        val ordering = COMPARATORS[credibleSide]!!
        val sortedPrices = marketPrices.sortedWith(ordering)
        checkState(sortedPrices.map { it.selectionId }.distinct().count() ==
                sortedPrices.map { it.selectionId }.count())

        for ((i, runnerPrices) in sortedPrices.withIndex()) {
            val selectionId = runnerPrices.selectionId
            val runner = market.getRunner(selectionId)
            val activeSelection = SideSelection(side, selectionId) in coverage || SideSelection(side.opposite, selectionId) in coverage
            val accepted = i in flowFilter.indexRange && flowFilter.runnerPredicate(market, runner)
            if (activeSelection || accepted) {
                val oldBet = coverage[SideSelection(side, selectionId)]
                val ctx = contextFactory.create(side = side,
                        selectionId = selectionId,
                        snapshot = snapshot,
                        fairness = fairness,
                        accountMoney = accountMoney)
                setTradedVolumeMean(ctx)
                val pricelessValidation = validationService.validate(ctx, validators)
                if (!pricelessValidation.isSuccess) {
                    cancelBet(oldBet, betCollector)
                    continue
                }

                val newPrice = priceAdviser.getNewPrice(ctx)
                if (newPrice == null) {
                    cancelBet(oldBet, betCollector)
                    continue
                }
                ctx.newPrice = newPrice
                if (oldBet != null && oldBet.isMatched) continue
                val priceValidation = validationService.validate(ctx, validators)
                if (priceValidation.isSuccess) {
                    bet(ctx, betCollector)
                }
            }
        }
    }

    private fun bet(ctx: BetContext, betCollector: BetCollector) {
        val action = ctx.createBetAction()
        val newPrice = ctx.newPrice!!

        val oldBet = ctx.oldBet
        if (oldBet != null) {
            betCollector.updateBet(BetCommand(oldBet.replacePrice(newPrice.price), action))
        } else {
            val market = ctx.market
            val bet = Bet(marketId = market.id,
                    placedDate = Instant.now(),
                    selectionId = ctx.runnerPrices.selectionId,
                    requestedPrice = newPrice)
            betCollector.placeBet(BetCommand(bet, action))
        }
        log.log(Level.INFO, "Bet {0} action ''{1}''", arrayOf(action.betActionType, action))
    }

    private fun cancelBet(oldBet: Bet?, betCollector: BetCollector) {
        if (oldBet != null && !oldBet.isMatched) {
            metricRegistry.counter("bet.cancel").inc()
            betCollector.cancelBet(oldBet)
            log.log(Level.INFO, "CANCEL_BET: unable propose price for bet ''{0}''", oldBet)
        }
    }

    private fun setTradedVolumeMean(context: BetContext) {
        val tradedVolume = context.actualTradedVolume
        if (tradedVolume != null) {
            val weightedMean = tradedVolume.weightedMean
            if (weightedMean != null) {
                setProperty(BetAction.TRADED_VOL_MEAN, weightedMean, context.properties)
            }
        }
    }

    private fun setProperty(key: String, value: Double, properties: MutableMap<String, String>) {
        properties[key] = Precision.round(value, 4).toString()
    }

    companion object {
        private val log = Logger.getLogger(AbstractUpdatingBettor::class.java.simpleName)
    }
}
