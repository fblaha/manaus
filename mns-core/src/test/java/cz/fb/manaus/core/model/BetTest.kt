package cz.fb.manaus.core.model

import com.fasterxml.jackson.databind.ObjectMapper
import cz.fb.manaus.core.provider.ExchangeProvider
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.core.test.CoreTestFactory
import org.junit.Assert.*
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

class BetTest : AbstractLocalTestCase() {

    @Autowired
    private val provider: ExchangeProvider? = null

    @Test
    fun `half matched`() {
        assertTrue(createBet(provider!!.minAmount).isHalfMatched)
        assertTrue(createBet(1.5).isHalfMatched)
        assertFalse(createBet(0.0).isHalfMatched)
        assertFalse(createBet(0.8).isHalfMatched)
    }

    @Test
    fun `fully matched`() {
        assertTrue(createBet(provider!!.minAmount).isMatched)
        assertTrue(createBet(1.5).isMatched)
        assertFalse(createBet(0.0).isMatched)
        assertTrue(createBet(0.8).isMatched)
    }

    @Test
    fun `json marshall`() {
        val mapper = ObjectMapper()
        val original = Bet("111", "222", 333,
                Price(3.0, 2.0, Side.BACK), Date(), 0.0)

        val serialized = mapper.writer().writeValueAsString(original)
        val restored = mapper.readerFor(Bet::class.java).readValue<Bet>(serialized)
        assertEquals(original.requestedPrice, restored.requestedPrice)
        assertEquals(original.placedDate, restored.placedDate)
        val doubleSerialized = mapper.writer().writeValueAsString(restored)
        assertEquals(serialized, doubleSerialized)
    }

    private fun createBet(matchedAmount: Double): Bet {
        val marketId = CoreTestFactory.MARKET_ID
        val selectionId = CoreTestFactory.DRAW
        val requestedPrice = Price(3.0, provider!!.minAmount, Side.LAY)
        val date = Instant.now().minus(2, ChronoUnit.HOURS)
        return Bet("1", marketId, selectionId, requestedPrice, Date.from(date), matchedAmount)
    }
}