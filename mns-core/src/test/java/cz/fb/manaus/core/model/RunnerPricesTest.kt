package cz.fb.manaus.core.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class RunnerPricesTest {

    @Test
    fun homogeneous() {
        val back = runnerPrices.first().getHomogeneous(Side.BACK)
        assertEquals(2, back.prices.size)
        assertEquals(Side.BACK, back.prices.first().side)
    }

    @Test
    fun `sorted prices`() {
        assertEquals(4, runnerPrices.first().sortedPrices.size)
        assertEquals(Side.BACK, runnerPrices.first().sortedPrices.first().side)
    }

    @Test
    fun `best price`() {
        assertEquals(Side.BACK, runnerPrices.first().bestPrice?.side)
    }
}