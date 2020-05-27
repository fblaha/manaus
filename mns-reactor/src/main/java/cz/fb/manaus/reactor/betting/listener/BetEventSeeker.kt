package cz.fb.manaus.reactor.betting.listener

import cz.fb.manaus.core.model.*
import cz.fb.manaus.reactor.betting.BetCommand
import cz.fb.manaus.reactor.betting.createBetEvent
import cz.fb.manaus.reactor.charge.ChargeGrowthForecaster
import cz.fb.manaus.reactor.price.FairnessPolynomialCalculator
import org.springframework.stereotype.Component
import java.util.logging.Logger

typealias MarketRunnerPredicate = (Market, Runner) -> Boolean

@Component
class BetEventSeeker(
        private val calculator: FairnessPolynomialCalculator,
        private val betEventNotifier: BetEventNotifier,
        private val chargeGrowthForecaster: ChargeGrowthForecaster,
        private val marketRunnerPredicate: MarketRunnerPredicate = { _, _ -> true }
) : MarketSnapshotListener {

    private val log = Logger.getLogger(BetEventSeeker::class.simpleName)

    override fun onMarketSnapshot(marketSnapshotEvent: MarketSnapshotEvent): List<BetCommand> {
        val (snapshot, account) = marketSnapshotEvent
        val (marketPrices, market) = snapshot
        val collector = mutableListOf<BetCommand>()
        val fairness = calculator.getFairness(marketPrices)
        for (runnerPrices in marketPrices) {
            val selectionId = runnerPrices.selectionId
            val runner = market.getRunner(selectionId)
            val accepted = marketRunnerPredicate(market, runner)
            if (snapshot.coverage.isActive(selectionId) || accepted) {
                for (side in betEventNotifier.activeSides) {
                    val sideSelection = SideSelection(side, selectionId)
                    val forecast = chargeGrowthForecaster.getForecast(
                            sideSelection = sideSelection,
                            snapshot = snapshot,
                            fairness = fairness,
                            commission = account.provider.commission
                    )
                    val betEvent = createBetEvent(
                            sideSelection = sideSelection,
                            snapshot = snapshot,
                            fairness = fairness,
                            account = account,
                            forecast = forecast
                    )
                    log.info { "bet event ${market.event.name} - ${runner.name} ($side)" }
                    collector.addAll(betEventNotifier.notify(betEvent))
                }
            }
        }
        return collector.toList()
    }
}
