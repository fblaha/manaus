package cz.fb.manaus.core.model

import com.google.common.collect.Comparators.isInOrder
import cz.fb.manaus.core.model.PriceComparator.CMP
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PriceComparatorTest {

    @Test
    fun testComparisonLay() {
        assertTrue(isInOrder(listOf(Price(2.0, 5.0, Side.LAY), Price(2.1, 5.0, Side.LAY)), CMP))
        assertTrue(isInOrder(listOf(Price(2.0, 5.0, Side.LAY), Price(2.1, 5.0, Side.LAY)), CMP))
        assertTrue(isInOrder(listOf(Price(2.0, 5.0, Side.LAY), Price(2.0, 5.0, Side.LAY)), CMP))
        assertFalse(isInOrder(listOf(Price(2.1, 5.0, Side.LAY), Price(2.0, 5.0, Side.LAY)), CMP))
    }

    @Test
    fun testComparisonBack() {
        assertTrue(isInOrder(listOf(Price(2.1, 5.0, Side.BACK), Price(2.0, 5.0, Side.BACK)), CMP))
        assertTrue(isInOrder(listOf(Price(2.0, 5.0, Side.BACK), Price(2.0, 5.0, Side.BACK)), CMP))
        assertFalse(isInOrder(listOf(Price(2.0, 5.0, Side.BACK), Price(2.1, 5.0, Side.BACK)), CMP))
    }

    @Test
    fun testComparison() {
        assertTrue(isInOrder(listOf(Price(2.0, 5.0, Side.BACK), Price(2.0, 5.0, Side.LAY)), CMP))
        assertFalse(isInOrder(listOf(Price(2.0, 5.0, Side.LAY), Price(2.0, 5.0, Side.BACK)), CMP))
    }
}
