package cz.fb.manaus.core.manager.filter

import cz.fb.manaus.core.model.Bet
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import org.junit.Test
import java.time.Instant
import kotlin.test.assertEquals

class UnknownBetsValidatorTest {

    @Test
    fun `unknown bets`() {
        val bet = Bet(
            betId = "1",
            marketId = "1",
            selectionId = 1,
            requestedPrice = Price(3.0, 3.0, Side.BACK),
            placedDate = Instant.now()
        )
        assertEquals(0, getUnknownBets(listOf(bet), setOf("1")).size)
        assertEquals(1, getUnknownBets(listOf(bet), setOf("2")).size)
    }

}