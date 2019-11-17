package cz.fb.manaus.reactor.betting.listener

import cz.fb.manaus.core.model.Account
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
            account: Account
    ): BetEvent {
        val forecast = forecaster.getForecast(
                sideSelection,
                snapshot = snapshot,
                fairness = fairness,
                commission = account.provider.commission
        )
        val metrics = BetMetrics(
                chargeGrowthForecast = forecast,
                fairness = fairness,
                actualTradedVolume = snapshot.tradedVolume?.get(key = sideSelection.selectionId)
        )
        return BetEvent(
                sideSelection = sideSelection,
                market = snapshot.market,
                marketPrices = snapshot.runnerPrices,
                account = account,
                coverage = snapshot.coverage,
                metrics = metrics
        )
    }
}
