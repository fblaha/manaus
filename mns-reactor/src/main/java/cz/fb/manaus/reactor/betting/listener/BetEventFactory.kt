package cz.fb.manaus.reactor.betting.listener

import cz.fb.manaus.core.model.Account
import cz.fb.manaus.core.model.Bet
import cz.fb.manaus.core.model.MarketSnapshot
import cz.fb.manaus.core.model.SideSelection
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.BetMetrics
import cz.fb.manaus.reactor.charge.ChargeGrowthForecaster
import cz.fb.manaus.reactor.price.Fairness
import org.springframework.stereotype.Component

@Component
class BetEventFactory(private val forecaster: ChargeGrowthForecaster) {

    fun create(
            sideSelection: SideSelection,
            snapshot: MarketSnapshot,
            fairness: Fairness,
            account: Account, coverage:
            Map<SideSelection, Bet>
    ): BetEvent {
        val (side, selectionId) = sideSelection
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
}
