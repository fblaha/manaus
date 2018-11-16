package cz.fb.manaus.reactor.betting

import cz.fb.manaus.core.model.AccountMoney
import cz.fb.manaus.core.model.MarketSnapshot
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.model.getRunnerPrices
import cz.fb.manaus.reactor.charge.ChargeGrowthForecaster
import cz.fb.manaus.reactor.price.Fairness
import org.springframework.stereotype.Component

@Component
class BetContextFactory(private val forecaster: ChargeGrowthForecaster) {

    fun create(side: Side,
               selectionId: Long,
               snapshot: MarketSnapshot,
               fairness: Fairness,
               accountMoney: AccountMoney? = null): BetContext {
        val (marketPrices, market, _, coverage, tradedVolume) = snapshot
        val forecast = forecaster.getForecast(selectionId, side, snapshot, fairness)
        return BetContext(
                market = market,
                side = side,
                selectionId = selectionId,
                marketPrices = marketPrices,
                runnerPrices = getRunnerPrices(marketPrices, selectionId),
                accountMoney = accountMoney,
                chargeGrowthForecast = forecast,
                coverage = coverage,
                fairness = fairness,
                actualTradedVolume = tradedVolume?.get(selectionId))
    }
}
