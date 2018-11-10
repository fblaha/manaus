package cz.fb.manaus.repository.domain

import org.junit.Test
import kotlin.test.assertEquals

val runnerPrices = RunnerPrices(
        selectionId = 100,
        matchedAmount = 100.0,
        lastMatchedPrice = 3.0,
        prices = listOf(
                Price(3.0, 100.0, Side.BACK),
                Price(3.5, 100.0, Side.LAY)
        )
)

class RunnerPricesTest {

    @Test
    fun homogeneous() {
        val back = runnerPrices.getHomogeneous(Side.BACK)
        assertEquals(1, back.prices.size)
        assertEquals(Side.BACK, back.prices.first().side)
    }

    @Test
    fun `sorted prices`() {
        val sorted = runnerPrices.sortedPrices
        assertEquals(2, sorted.size)
        assertEquals(Side.BACK, sorted.first().side)
    }

    @Test
    fun `best price`() {
        val back = runnerPrices.bestPrice
        assertEquals(Side.BACK, back?.side)
    }
}