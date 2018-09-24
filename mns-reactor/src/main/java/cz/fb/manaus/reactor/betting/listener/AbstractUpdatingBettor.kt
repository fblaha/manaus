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
import java.util.logging.Level
import java.util.logging.Logger

abstract class AbstractUpdatingBettor protected constructor(private val side: Side, private val validators: List<Validator>, private val priceAdviser: PriceAdviser) : MarketSnapshotListener {
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
        val marketPrices = snapshot.marketPrices
        val market = marketPrices.market
        val winnerCount = marketPrices.winnerCount
        val flowFilter = flowFilterRegistry.getFlowFilter(market.type)
        if (flowFilter.winnerCountRange.contains(winnerCount)) {
            val coverage = snapshot.coverage
            val fairness = calculator.getFairness(marketPrices)
            val credibleSide = fairness.moreCredibleSide!!
            val ordering = COMPARATORS[credibleSide]!!
            val prices = marketPrices.runnerPrices.sortedWith(ordering)
            checkState(prices.map { it.selectionId }.distinct().count() ==
                    prices.map { it.selectionId }.count())

            for (i in prices.indices) {
                val runnerPrices = prices[i]
                val selectionId = runnerPrices.selectionId
                val runner = market.getRunner(selectionId)
                val activeSelection = coverage.contains(side, selectionId) || coverage.contains(side.opposite, selectionId)
                val accepted = flowFilter.indexRange.contains(i) && flowFilter.runnerPredicate(market, runner)
                if (activeSelection || accepted) {
                    val oldBet = coverage.get(side, selectionId)
                    val ctx = contextFactory.create(side, selectionId,
                            snapshot, fairness, accountMoney)
                    setTradedVolumeMean(ctx)
                    val pricelessValidation = validationService.validate(ctx, validators)
                    if (!pricelessValidation.isSuccess) {
                        cancelBet(oldBet, betCollector)
                        continue
                    }

                    val newPrice = priceAdviser.getNewPrice(ctx)
                    if (!newPrice.isPresent) {
                        cancelBet(oldBet, betCollector)
                        continue
                    }

                    val priceCtx = ctx.withNewPrice(newPrice.get())

                    if (oldBet != null && oldBet.isMatched) continue

                    val priceValidation = validationService.validate(priceCtx, validators)

                    if (priceValidation.isSuccess) {
                        bet(priceCtx, betCollector)
                    }
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
            val market = ctx.marketPrices.market
            val bet = Bet(null, market.id, ctx.runnerPrices.selectionId, newPrice, null, 0.0)
            betCollector.placeBet(BetCommand(bet, action))
        }
        log.log(Level.INFO, "{0}_BET:  new bet ''{1}''", arrayOf(action.betActionType, action))
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
        if (tradedVolume.isPresent) {
            val weightedMean = tradedVolume.get().weightedMean
            if (weightedMean.isPresent) {
                setProperty(BetAction.TRADED_VOL_MEAN, weightedMean.asDouble, context.properties)
            }
        }
    }

    private fun setProperty(key: String, value: Double, properties: MutableMap<String, String>) {
        properties[key] = java.lang.Double.toString(Precision.round(value, 4))
    }

    companion object {
        private val log = Logger.getLogger(AbstractUpdatingBettor::class.java.simpleName)
    }
}
