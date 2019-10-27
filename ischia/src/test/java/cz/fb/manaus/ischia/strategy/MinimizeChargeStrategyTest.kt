package cz.fb.manaus.ischia.strategy

import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.HOME_EVENT
import org.junit.Assert.assertEquals
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("ischia")
class MinimizeChargeStrategyTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var strategy: MinimizeChargeStrategy

    @Test
    fun strategy() {
        val event = HOME_EVENT
        assertEquals(strategy.getUpperBoundary(event.side), strategy(event.replaceForecast(null)), 0.000001)
        assertEquals(strategy.getUpperBoundary(event.side), strategy(event.replaceForecast(Double.NaN)), 0.000001)
        assertEquals(strategy.getUpperBoundary(event.side), strategy(event.replaceForecast(1.5)), 0.000001)
        assertEquals(strategy.fairnessReductionLow, strategy(event.replaceForecast(0.1)), 0.000001)
    }

    private fun BetEvent.replaceForecast(forecast: Double?) =
            copy(metrics = metrics.copy(chargeGrowthForecast = forecast))
}