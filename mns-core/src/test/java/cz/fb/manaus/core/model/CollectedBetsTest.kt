package cz.fb.manaus.core.model

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.*

class CollectedBetsTest {

    @Test
    fun testSerialization() {
        val mapper = ObjectMapper()
        val original = Bet("111", "222", 333,
                Price(3.0, 2.0, Side.BACK), Date(), 0.0)

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