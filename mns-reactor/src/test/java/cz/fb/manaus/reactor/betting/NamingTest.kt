package cz.fb.manaus.reactor.betting

import cz.fb.manaus.core.makeName
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import org.junit.Test
import kotlin.test.assertEquals

class NamingTest {

    @Test
    fun `default name`() {
        assertEquals("veryProfitableProposer", makeName(VeryProfitableProposer()))
    }

    private class VeryProfitableProposer : PriceProposer {

        override fun getProposedPrice(event: BetEvent): Double {
            return 5.0
        }
    }

}