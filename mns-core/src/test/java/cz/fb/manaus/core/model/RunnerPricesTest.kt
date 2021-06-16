package cz.fb.manaus.core.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class RunnerPricesTest {

    @Test
    fun homogeneous() {
        val back = runnerPrices.first().by(Side.BACK)
        assertEquals(2, back.prices.size)
        assertEquals(Side.BACK, back.prices.first().side)
    }

    @Test
    fun `best price`() {
        val lay = runnerPrices.first().by(Side.LAY)
        val back = runnerPrices.first().by(Side.BACK)
        assertEquals(2.5, back.bestPrice?.price)
        assertEquals(3.5, lay.bestPrice?.price)
    }
}