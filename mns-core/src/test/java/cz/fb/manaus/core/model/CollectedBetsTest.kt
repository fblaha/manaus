package cz.fb.manaus.core.model

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertTrue

class CollectedBetsTest {

    @Test
    fun serialization() {
        val mapper = ObjectMapper()
        val original = Bet(
                betId = "111", marketId = "222", selectionId = 333,
                requestedPrice = Price(3.0, 2.0, Side.BACK), placedDate = Instant.now()
        )

        val bets = CollectedBets(
                place = listOf(original.asTracked),
                update = listOf(original.asTracked),
                cancel = listOf("100")
        )

        val serialized = mapper.writer().writeValueAsString(bets)
        val tree = mapper.reader().readTree(serialized)
        assertTrue(tree.has("place"))
        assertTrue(tree.has("update"))
        assertTrue(tree.has("cancel"))
    }
}


val Bet.asTracked: TrackedBet
    get() = TrackedBet(
            this,
            betAction.copy(
                    marketId = marketId,
                    selectionId = selectionId,
                    price = requestedPrice
            ),
    )