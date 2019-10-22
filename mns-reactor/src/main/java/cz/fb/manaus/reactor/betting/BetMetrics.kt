package cz.fb.manaus.reactor.betting

import cz.fb.manaus.core.model.TradedVolume
import cz.fb.manaus.reactor.price.Fairness

data class BetMetrics(
        val chargeGrowthForecast: Double?,
        val fairness: Fairness,
        val actualTradedVolume: TradedVolume?
)