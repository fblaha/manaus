package cz.fb.manaus.ischia.strategy

import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.HOME_EVENT_BACK
import org.junit.Assert.assertEquals
import org.junit.Test

class MinimizeChargeStrategyTest {

    private val strategy: MinimizeChargeStrategy = MinimizeChargeStrategy(
            fairnessReductionLow = 0.01,
            fairnessReductionHighBack = 0.05,
            fairnessReductionHighLay = 0.06
    )

    @Test
    fun strategy() {
        val event = HOME_EVENT_BACK
        assertEquals(strategy.getUpperBoundary(event.side), strategy(event.replaceForecast(null)), 0.000001)
        assertEquals(strategy.getUpperBoundary(event.side), strategy(event.replaceForecast(Double.NaN)), 0.000001)
        assertEquals(strategy.getUpperBoundary(event.side), strategy(event.replaceForecast(1.5)), 0.000001)
        assertEquals(strategy.fairnessReductionLow, strategy(event.replaceForecast(0.1)), 0.000001)
    }

    private fun BetEvent.replaceForecast(forecast: Double?) =
            copy(metrics = metrics.copy(chargeGrowthForecast = forecast))
}