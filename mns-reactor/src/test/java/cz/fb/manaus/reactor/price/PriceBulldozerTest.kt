package cz.fb.manaus.reactor.price

import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.PriceComparator
import cz.fb.manaus.core.model.Side
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PriceBulldozerTest {

    private val realSample = listOf(
        Price(6.0, 2.0, Side.BACK),
        Price(3.0, 4.0, Side.BACK),
        Price(2.8, 10.0, Side.BACK)
    )

    @Test
    fun `bulldoze lay prices`() {
        checkResult(3.0, listOf(Price(3.0, 2.0, Side.LAY), Price(4.0, 2.0, Side.LAY)), 1, 3.5, 4.0)
        val three = listOf(
            Price(3.0, 2.0, Side.LAY),
            Price(4.0, 2.0, Side.LAY), Price(5.0, 2.0, Side.LAY)
        )
        checkResult(3.0, three, 2, 3.5, 4.0)
        checkResult(5.0, three, 1, 4.0, 6.0)
    }

    @Test
    fun `bulldoze - only 1 price`() {
        checkResult(3.0, listOf(Price(3.0, 2.0, Side.LAY)), 1, 3.0, 2.0)
        checkResult(1.0, listOf(Price(3.0, 2.0, Side.LAY)), 1, 3.0, 2.0)
    }

    @Test
    fun `threshold equal to amount sum`() {
        val two = listOf(Price(4.0, 2.0, Side.BACK), Price(3.0, 2.0, Side.BACK))
        val three = listOf(
            Price(5.0, 2.0, Side.BACK), Price(4.0, 2.0, Side.BACK),
            Price(3.0, 2.0, Side.BACK)
        )

        checkResult(2.0, two, 2, 4.0, 2.0)
        checkResult(4.0, three, 2, 4.5, 4.0)
    }

    @Test
    fun `bulldoze real data`() {
        checkResult(1.0, realSample, 3, 6.0, 2.0)
        checkResult(2.0, realSample, 3, 6.0, 2.0)
        checkResult(3.0, realSample, 2, 4.0, 6.0)
        checkResult(4.0, realSample, 2, 4.0, 6.0)
        checkResult(5.0, realSample, 2, 4.0, 6.0)
        checkResult(6.0, realSample, 2, 4.0, 6.0)
        checkResult(50.0, realSample, 1, 3.25, 16.0)
        checkResult(100.0, realSample, 1, 3.25, 16.0)
    }

    fun `bulldozed prices are in wrong order - back`() {
        assertFailsWith<IllegalStateException> {
            val badOrder = realSample.sortedWith(PriceComparator).reversed()
            PriceBulldozer.bulldoze(10.0, badOrder)
        }
    }

    @Test
    fun `bulldozed prices are in wrong order - lay`() {
        assertFailsWith<IllegalStateException> {
            PriceBulldozer.bulldoze(
                10.0,
                listOf(Price(5.0, 2.0, Side.LAY), Price(4.0, 2.0, Side.LAY))
            )

        }
    }

    private fun checkResult(
        threshold: Double,
        prices: List<Price>,
        expectedCount: Int,
        expectedPrice: Double,
        expectedAmount: Double
    ) {
        val bulldozed = PriceBulldozer.bulldoze(threshold, prices)
        assertEquals(expectedCount, bulldozed.size)
        assertEquals(expectedPrice, bulldozed[0].price, 0.0001)
        assertEquals(expectedAmount, bulldozed[0].amount, 0.0001)

        assertEquals(
            prices.sumOf { it.amount },
            bulldozed.sumOf { it.amount }, 0.0001
        )
        assertEquals(
            getWeightedMean(prices)!!,
            getWeightedMean(bulldozed)!!, 0.0001
        )
    }

}