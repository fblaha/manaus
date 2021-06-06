package cz.fb.manaus.reactor.betting.proposer

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.HOME_EVENT_BACK
import cz.fb.manaus.reactor.betting.HOME_EVENT_LAY
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PriceProposalServiceTest {

    private val service = PriceProposalService

    @Test
    fun `back price`() {
        checkProposal(MAX_PRICE, Side.BACK, PROPOSERS)
    }

    @Test
    fun `lay price`() {
        checkProposal(MIN_PRICE, Side.LAY, PROPOSERS)
    }

    @Test
    fun `mandatory price`() {
        val proposer: PriceProposer = object : PriceProposer {
            override fun getProposedPrice(event: BetEvent): Double? {
                return null
            }
        }
        assertFailsWith<IllegalStateException> {
            service.reducePrices(HOME_EVENT_LAY, listOf(proposer))
        }
    }

    private fun checkProposal(expectedPrice: Double, side: Side, proposers: List<PriceProposer>) {
        val event = when (side) {
            Side.LAY -> HOME_EVENT_LAY
            Side.BACK -> HOME_EVENT_BACK
        }
        val price = service.reducePrices(event, proposers).price
        assertEquals(expectedPrice, price)
    }

    private class TestProposer1 : PriceProposer {

        override fun getProposedPrice(event: BetEvent): Double {
            return MIN_PRICE
        }
    }

    private open class TestProposer2 : PriceProposer {

        override fun getProposedPrice(event: BetEvent): Double {
            return MAX_PRICE
        }
    }

    private class FooProposer : TestProposer2()

    companion object {

        val PROPOSERS = listOf(
            TestProposer1(),
            TestProposer2(),
            FooProposer()
        )
        const val MIN_PRICE = 1.5
        const val MAX_PRICE = 2.0
    }
}