package cz.fb.manaus.core.repository.domain

import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class PriceComparatorTest {

    private fun isSorted(l: List<Price>): Boolean {
        return l == l.sortedWith(PriceComparator)
    }

    @Test
    fun testComparisonLay() {
        assertTrue(isSorted(listOf(Price(2.0, 5.0, Side.LAY), Price(2.1, 5.0, Side.LAY))))
        assertTrue(isSorted(listOf(Price(2.0, 5.0, Side.LAY), Price(2.1, 5.0, Side.LAY))))
        assertTrue(isSorted(listOf(Price(2.0, 5.0, Side.LAY), Price(2.0, 5.0, Side.LAY))))
        assertFalse(isSorted(listOf(Price(2.1, 5.0, Side.LAY), Price(2.0, 5.0, Side.LAY))))
    }

    @Test
    fun testComparisonBack() {
        assertTrue(isSorted(listOf(Price(2.1, 5.0, Side.BACK), Price(2.0, 5.0, Side.BACK))))
        assertTrue(isSorted(listOf(Price(2.0, 5.0, Side.BACK), Price(2.0, 5.0, Side.BACK))))
        assertFalse(isSorted(listOf(Price(2.0, 5.0, Side.BACK), Price(2.1, 5.0, Side.BACK))))
    }

    @Test
    fun testComparison() {
        assertTrue(isSorted(listOf(Price(2.0, 5.0, Side.BACK), Price(2.0, 5.0, Side.LAY))))
        assertFalse(isSorted(listOf(Price(2.0, 5.0, Side.LAY), Price(2.0, 5.0, Side.BACK))))
    }
}


