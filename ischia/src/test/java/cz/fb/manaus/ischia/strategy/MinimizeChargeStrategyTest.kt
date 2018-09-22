package cz.fb.manaus.ischia.strategy

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import cz.fb.manaus.core.model.Event
import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.model.MarketPrices
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.reactor.betting.BetContext
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@ActiveProfiles("ischia")
class MinimizeChargeStrategyTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var strategy: MinimizeChargeStrategy

    @Test
    fun strategy() {
        val context = mock<BetContext>()
        whenever(context.side).thenReturn(Side.BACK)
        val marketPrices = Mockito.mock(MarketPrices::class.java)
        val market = Mockito.mock(Market::class.java)
        val event = Mockito.mock(Event::class.java)
        val openDate = Date.from(Instant.now().plus(30, ChronoUnit.MINUTES))
        whenever(event.openDate).thenReturn(openDate)
        whenever(market.event).thenReturn(event)
        whenever(marketPrices.market).thenReturn(market)
        whenever(context.marketPrices).thenReturn(marketPrices)


        whenever(context.chargeGrowthForecast).thenReturn(
                null,
                1.0 / 0.0,
                1.5,
                0.1)
        assertEquals(strategy.getUpperBoundary(context.side), strategy.getReductionRate(context), 0.000001)
        assertEquals(strategy.getUpperBoundary(context.side), strategy.getReductionRate(context), 0.000001)
        assertEquals(strategy.getUpperBoundary(context.side), strategy.getReductionRate(context), 0.000001)
        assertEquals(strategy.fairnessReductionLow, strategy.getReductionRate(context), 0.000001)
    }


}