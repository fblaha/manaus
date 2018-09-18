package cz.fb.manaus.reactor.betting.proposer

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.reactor.betting.BetContext
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*
import kotlin.test.assertEquals

class PriceProposalServiceTest : AbstractLocalTestCase() {
    @Autowired
    private lateinit var service: PriceProposalService

    @Test
    fun `back price`() {
        checkProposal(MAX_PRICE, Side.BACK, PROPOSERS)
    }

    @Test
    fun `lay price`() {
        checkProposal(MIN_PRICE, Side.LAY, PROPOSERS)
    }

    @Test(expected = IllegalStateException::class)
    fun `mandatory price`() {
        val proposer: PriceProposer = object : PriceProposer {
            override fun getProposedPrice(context: BetContext): OptionalDouble {
                return OptionalDouble.empty()
            }
        }
        service.reducePrices(mock(), listOf(proposer), Side.LAY)
    }

    private fun checkProposal(expectedPrice: Double, side: Side, proposers: List<PriceProposer>) {
        val context: BetContext = mock()
        whenever(context.properties).thenReturn(HashMap())
        val price = service.reducePrices(context, proposers, side).price
        assertEquals(expectedPrice, price)
    }

    private class TestProposer1 : PriceProposer {

        override fun getProposedPrice(context: BetContext): OptionalDouble {
            return OptionalDouble.of(MIN_PRICE)
        }
    }

    private open class TestProposer2 : PriceProposer {

        override fun getProposedPrice(context: BetContext): OptionalDouble {
            return OptionalDouble.of(MAX_PRICE)
        }
    }

    private class FooProposer : TestProposer2()

    companion object {

        val PROPOSERS = listOf(
                TestProposer1(),
                TestProposer2(),
                FooProposer())
        const val MIN_PRICE = 1.5
        const val MAX_PRICE = 2.0
    }
}