package cz.fb.manaus.core.model

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.*

class CollectedBetsTest {

    @Test
    fun serialization() {
        val mapper = ObjectMapper()
        val original = Bet(betId = "111", marketId = "222", selectionId = 333,
                requestedPrice = Price(3.0, 2.0, Side.BACK), placedDate = Date())

        val bets = CollectedBets.create()
        bets.place.add(original)
        bets.update.add(original)
        bets.cancel.add("100")

        val serialized = mapper.writer().writeValueAsString(bets)
        val tree = mapper.reader().readTree(serialized)
        assertTrue(tree.has("place"))
        assertTrue(tree.has("update"))
        assertTrue(tree.has("cancel"))
    }
}