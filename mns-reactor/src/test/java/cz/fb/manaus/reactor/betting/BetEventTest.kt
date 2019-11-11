package cz.fb.manaus.reactor.betting

import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.reactor.ReactorTestFactory
import cz.fb.manaus.reactor.price.Fairness
import org.junit.Assert
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

val HOME_EVENT: BetEvent = BetEvent(
        selectionId = SEL_HOME,
        side = Side.BACK,
        account = account,
        coverage = mutableMapOf(),
        marketPrices = runnerPrices,
        market = market,
        metrics = BetMetrics(
                actualTradedVolume = tradedVolume[SEL_HOME],
                fairness = Fairness(0.9, 1.1),
                chargeGrowthForecast = 1.0
        )
)


class BetEventTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var factory: ReactorTestFactory

    @Test
    fun `counter half matched`() {
        assertTrue(factory.newBetEvent(Side.BACK, 3.5, 4.6).isCounterHalfMatched)
        assertTrue(factory.newBetEvent(Side.LAY, 3.5, 4.6).isCounterHalfMatched)
    }

    @Test
    fun `old matched`() {
        assertFalse { factory.newBetEvent(Side.BACK, 3.5, 4.6).isOldMatched }
    }

    @Test
    fun `simulate settled bet`() {
        val context = factory.newBetEvent(Side.LAY, 3.5, 4.6)
        context.newPrice = Price(3.0, 5.0, Side.LAY)
        val settledBet = context.simulatedBet
        assertEquals(context.side, settledBet.settledBet.price.side)
        Assert.assertEquals(5.0, settledBet.settledBet.price.amount, 0.0001)
        assertNotNull(settledBet.betAction)
    }
}