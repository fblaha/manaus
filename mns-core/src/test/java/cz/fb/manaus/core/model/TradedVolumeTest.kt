package cz.fb.manaus.core.model

import org.junit.Test
import kotlin.test.assertEquals

class TradedVolumeTest {

    @Test
    fun `weighted mean`() {
        assertEquals(2.0, TradedVolume(listOf(
                Price(2.0, 5.0, null))).weightedMean)
        assertEquals(2.0, TradedVolume(listOf(
                Price(2.0, 5.0, null),
                Price(2.0, 10.0, null))).weightedMean)
        assertEquals(5.0, TradedVolume(listOf(
                Price(3.0, 5.0, null),
                Price(6.0, 10.0, null))).weightedMean)
        assertEquals(7.0, TradedVolume(listOf(
                Price(3.0, 5.0, null),
                Price(9.0, 10.0, null))).weightedMean)
    }
}