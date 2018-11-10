package cz.fb.manaus.core.model

import org.junit.Test
import kotlin.test.assertEquals

class TradedVolumeTest {

    @Test
    fun `weighted mean`() {
        assertEquals(2.0, TradedVolume(listOf(
                TradedAmount(2.0, 5.0))).weightedMean)
        assertEquals(2.0, TradedVolume(listOf(
                TradedAmount(2.0, 5.0),
                TradedAmount(2.0, 10.0))).weightedMean)
        assertEquals(5.0, TradedVolume(listOf(
                TradedAmount(3.0, 5.0),
                TradedAmount(6.0, 10.0))).weightedMean)
        assertEquals(7.0, TradedVolume(listOf(
                TradedAmount(3.0, 5.0),
                TradedAmount(9.0, 10.0))).weightedMean)
    }
}