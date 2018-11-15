package cz.fb.manaus.ischia.strategy

import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("ischia")
class MinimizeChargeStrategyTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var strategy: MinimizeChargeStrategy

//    @Test
//    fun strategy() {
//        val context = mock<BetContext>()
//        whenever(context.side).thenReturn(Side.BACK)
//        val marketPrices = mock<MarketPrices>()
//        val market = mock<Market>()
//        val event = mock<Event>()
//        val openDate = Date.from(Instant.now().plus(30, ChronoUnit.MINUTES))
//        whenever(event.openDate).thenReturn(openDate)
//        whenever(market.event).thenReturn(event)
//        whenever(marketPrices.market).thenReturn(market)
//        whenever(context.marketPrices).thenReturn(marketPrices)
//
//
//        whenever(context.chargeGrowthForecast).thenReturn(
//                null,
//                Double.NaN,
//                1.5,
//                0.1)
//        assertEquals(strategy.getUpperBoundary(context.side), strategy.getReductionRate(context), 0.000001)
//        assertEquals(strategy.getUpperBoundary(context.side), strategy.getReductionRate(context), 0.000001)
//        assertEquals(strategy.getUpperBoundary(context.side), strategy.getReductionRate(context), 0.000001)
//        assertEquals(strategy.fairnessReductionLow, strategy.getReductionRate(context), 0.000001)
//    }


}