package cz.fb.manaus.reactor.betting

import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import org.junit.Test
import kotlin.test.assertEquals

class NameAwareTest {

    @Test
    fun `default name`() {
        assertEquals("veryProfitableProposer", VeryProfitableProposer().name)
    }

    private class VeryProfitableProposer : PriceProposer {

        override fun getProposedPrice(context: BetContext): Double {
            return 5.0
        }
    }

}