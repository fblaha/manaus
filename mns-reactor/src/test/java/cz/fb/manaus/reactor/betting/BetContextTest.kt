package cz.fb.manaus.reactor.betting

import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.reactor.ReactorTestFactory
import org.junit.Assert
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BetContextTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var testFactory: ReactorTestFactory

    @Test
    fun `counter half matched`() {
        assertTrue(testFactory.createContext(Side.BACK, 3.5, 4.6).isCounterHalfMatched)
        assertTrue(testFactory.createContext(Side.LAY, 3.5, 4.6).isCounterHalfMatched)
    }

    @Test
    fun `simulate settled bet`() {
        val context = testFactory.createContext(Side.LAY, 3.5, 4.6)
        context.newPrice = Price(3.0, 5.0, Side.LAY)
        val settledBet = context.simulateSettledBet()
        assertEquals(context.side, settledBet.price.side)
        Assert.assertEquals(5.0, settledBet.price.amount, 0.0001)
        assertNotNull(settledBet.betAction)
    }
}