package cz.fb.manaus.core.model

import cz.fb.manaus.core.model.PriceComparator.ORDERING
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PriceComparatorTest {

    @Test
    fun testComparisonLay() {
        assertTrue(ORDERING.isOrdered(listOf(Price(2.0, 5.0, Side.LAY), Price(2.1, 5.0, Side.LAY))))
        assertTrue(ORDERING.isOrdered(listOf(Price(2.0, 5.0, Side.LAY), Price(2.0, 5.0, Side.LAY))))
        assertFalse(ORDERING.isOrdered(listOf(Price(2.1, 5.0, Side.LAY), Price(2.0, 5.0, Side.LAY))))
    }

    @Test
    fun testComparisonBack() {
        assertTrue(ORDERING.isOrdered(listOf(Price(2.1, 5.0, Side.BACK), Price(2.0, 5.0, Side.BACK))))
        assertTrue(ORDERING.isOrdered(listOf(Price(2.0, 5.0, Side.BACK), Price(2.0, 5.0, Side.BACK))))
        assertFalse(ORDERING.isOrdered(listOf(Price(2.0, 5.0, Side.BACK), Price(2.1, 5.0, Side.BACK))))
    }

    @Test
    fun testComparison() {
        assertTrue(ORDERING.isOrdered(listOf(Price(2.0, 5.0, Side.BACK), Price(2.0, 5.0, Side.LAY))))
        assertFalse(ORDERING.isOrdered(listOf(Price(2.0, 5.0, Side.LAY), Price(2.0, 5.0, Side.BACK))))
    }
}
