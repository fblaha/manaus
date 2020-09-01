package cz.fb.manaus.core.model

import com.fasterxml.jackson.databind.ObjectMapper
import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.junit.Assert.*
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant
import java.time.temporal.ChronoUnit

val betTemplate = Bet(
    betId = "111",
    marketId = "222",
    selectionId = SEL_HOME,
    requestedPrice = Price(3.0, 2.0, Side.BACK),
    placedDate = Instant.now()
)


class BetTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var mapper: ObjectMapper

    @Test
    fun `half matched`() {
        assertTrue(createBet(mbProvider.minAmount).isHalfMatched)
        assertTrue(createBet(1.5).isHalfMatched)
        assertFalse(createBet(0.0).isHalfMatched)
        assertFalse(createBet(0.8).isHalfMatched)
    }

    @Test
    fun `fully matched`() {
        assertTrue(createBet(mbProvider.minAmount).isMatched)
        assertTrue(createBet(1.5).isMatched)
        assertFalse(createBet(0.0).isMatched)
        assertTrue(createBet(0.8).isMatched)
    }

    @Test
    fun `json marshall`() {

        val serialized = mapper.writer().writeValueAsString(betTemplate)
        val restored = mapper.readerFor(Bet::class.java).readValue<Bet>(serialized)
        assertEquals(betTemplate.requestedPrice, restored.requestedPrice)
        assertEquals(betTemplate.placedDate, restored.placedDate)
        val doubleSerialized = mapper.writer().writeValueAsString(restored)
        assertEquals(serialized, doubleSerialized)
    }

    private fun createBet(matchedAmount: Double): Bet {
        val requestedPrice = Price(3.0, mbProvider.minAmount, Side.LAY)
        val date = Instant.now().minus(2, ChronoUnit.HOURS)
        return Bet(
            betId = "1",
            marketId = market.id,
            selectionId = 1000L,
            requestedPrice = requestedPrice,
            placedDate = date,
            matchedAmount = matchedAmount
        )
    }
}