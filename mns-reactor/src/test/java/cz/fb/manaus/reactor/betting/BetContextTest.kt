package cz.fb.manaus.reactor.betting

import com.google.common.collect.HashBasedTable
import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.reactor.ReactorTestFactory
import cz.fb.manaus.reactor.price.Fairness
import org.junit.Assert
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

val homeContext: BetContext = BetContext(
        selectionId = SEL_HOME,
        side = Side.BACK,
        actualTradedVolume = tradedVolume[SEL_HOME],
        accountMoney = accountMoney,
        coverage = HashBasedTable.create(),
        fairness = Fairness(0.9, 1.1),
        chargeGrowthForecast = 1.0,
        marketPrices = runnerPrices,
        market = market)


class BetContextTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var factory: ReactorTestFactory

    @Test
    fun `counter half matched`() {
        assertTrue(factory.newBetContext(Side.BACK, 3.5, 4.6).isCounterHalfMatched)
        assertTrue(factory.newBetContext(Side.LAY, 3.5, 4.6).isCounterHalfMatched)
    }

    @Test
    fun `simulate settled bet`() {
        val context = factory.newBetContext(Side.LAY, 3.5, 4.6)
        context.newPrice = Price(3.0, 5.0, Side.LAY)
        val settledBet = context.simulateSettledBet()
        assertEquals(context.side, settledBet.settledBet.price.side)
        Assert.assertEquals(5.0, settledBet.settledBet.price.amount, 0.0001)
        assertNotNull(settledBet.betAction)
    }
}