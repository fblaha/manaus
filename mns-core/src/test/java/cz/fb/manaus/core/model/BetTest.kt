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
    private lateinit var provider: ExchangeProvider
    @Autowired
    private lateinit var mapper: ObjectMapper

    @Test
    fun `half matched`() {
        assertTrue(createBet(provider.minAmount).isHalfMatched)
        assertTrue(createBet(1.5).isHalfMatched)
        assertFalse(createBet(0.0).isHalfMatched)
        assertFalse(createBet(0.8).isHalfMatched)
    }

    @Test
    fun `fully matched`() {
        assertTrue(createBet(provider.minAmount).isMatched)
        assertTrue(createBet(1.5).isMatched)
        assertFalse(createBet(0.0).isMatched)
        assertTrue(createBet(0.8).isMatched)
    }

    @Test
    fun `json marshall`() {
        val original = Bet(betId = "111", marketId = "222", selectionId = 333,
                requestedPrice = Price(3.0, 2.0, Side.BACK),
                placedDate = Date())

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
        val requestedPrice = Price(3.0, provider.minAmount, Side.LAY)
        val date = Instant.now().minus(2, ChronoUnit.HOURS)
        return Bet(betId = "1", marketId = marketId, selectionId = selectionId,
                requestedPrice = requestedPrice, placedDate = Date.from(date), matchedAmount = matchedAmount)
    }
}