package cz.fb.manaus.ischia.strategy

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.reactor.betting.BetContext
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
        val context = mock<BetContext>()
        whenever(context.side).thenReturn(Side.BACK)
        whenever(context.chargeGrowthForecast).thenReturn(null, Double.NaN, 1.5, 0.1)
        assertEquals(strategy.getUpperBoundary(context.side), strategy.getReductionRate(context), 0.000001)
        assertEquals(strategy.getUpperBoundary(context.side), strategy.getReductionRate(context), 0.000001)
        assertEquals(strategy.getUpperBoundary(context.side), strategy.getReductionRate(context), 0.000001)
        assertEquals(strategy.fairnessReductionLow, strategy.getReductionRate(context), 0.000001)
    }
}