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
    private lateinit var factory: ReactorTestFactory

    @Test
    fun `counter half matched`() {
        assertTrue(factory.createContext(Side.BACK, 3.5, 4.6).isCounterHalfMatched)
        assertTrue(factory.createContext(Side.LAY, 3.5, 4.6).isCounterHalfMatched)
    }

    @Test
    fun `simulate settled bet`() {
        val context = factory.createContext(Side.LAY, 3.5, 4.6)
        context.newPrice = Price(3.0, 5.0, Side.LAY)
        val settledBet = context.simulateSettledBet()
        assertEquals(context.side, settledBet.settledBet.price.side)
        Assert.assertEquals(5.0, settledBet.settledBet.price.amount, 0.0001)
        assertNotNull(settledBet.betAction)
    }
}