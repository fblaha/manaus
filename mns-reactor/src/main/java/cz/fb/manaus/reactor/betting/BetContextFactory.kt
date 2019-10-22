package cz.fb.manaus.reactor.betting

import cz.fb.manaus.core.model.Account
import cz.fb.manaus.core.model.MarketSnapshot
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.charge.ChargeGrowthForecaster
import cz.fb.manaus.reactor.price.Fairness
import org.springframework.stereotype.Component

@Component
class BetContextFactory(private val forecaster: ChargeGrowthForecaster) {

    fun create(side: Side,
               selectionId: Long,
               snapshot: MarketSnapshot,
               fairness: Fairness,
               account: Account): BetContext {
        val (marketPrices, market, _, coverage, tradedVolume) = snapshot
        val commission = account.provider.commission
        val forecast = forecaster.getForecast(selectionId, side, snapshot, fairness, commission)
        val metrics = Metrics(
                chargeGrowthForecast = forecast,
                fairness = fairness,
                actualTradedVolume = tradedVolume?.get(selectionId)
        )
        return BetContext(
                market = market,
                side = side,
                selectionId = selectionId,
                marketPrices = marketPrices,
                account = account,
                coverage = coverage,
                metrics = metrics
        )
    }
}
