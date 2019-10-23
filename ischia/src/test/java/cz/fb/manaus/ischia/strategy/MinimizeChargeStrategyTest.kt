package cz.fb.manaus.ischia.strategy

import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.betting.homeContext
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
        val ctx = homeContext
        assertEquals(strategy.getUpperBoundary(ctx.side), strategy(ctx.replaceForecast(null)), 0.000001)
        assertEquals(strategy.getUpperBoundary(ctx.side), strategy(ctx.replaceForecast(Double.NaN)), 0.000001)
        assertEquals(strategy.getUpperBoundary(ctx.side), strategy(ctx.replaceForecast(1.5)), 0.000001)
        assertEquals(strategy.fairnessReductionLow, strategy(ctx.replaceForecast(0.1)), 0.000001)
    }

    private fun BetContext.replaceForecast(forecast: Double?) =
            copy(metrics = metrics.copy(chargeGrowthForecast = forecast))
}