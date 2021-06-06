package cz.fb.manaus.reactor.betting.listener

import cz.fb.manaus.core.model.RunnerPrices
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.PricesTestFactory
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ProbabilityComparatorTest {

    @Test
    fun `probability comparison`() {
        val first = PricesTestFactory.newRunnerPrices(1, 1.4, 1.6)
        val second = PricesTestFactory.newRunnerPrices(2, 2.8, 3.3)
        assertEquals(1L, getFirstSelection(listOf(first, first)))
        assertEquals(1L, getFirstSelection(listOf(first, second)))
        assertEquals(1L, getFirstSelection(listOf(second, first)))
        assertEquals(2L, getFirstSelection(listOf(second, second)))
    }

    private fun getFirstSelection(lists: List<RunnerPrices>): Long {
        return lists.sortedWith(ProbabilityComparator.COMPARATORS.getValue(Side.BACK))[0].selectionId
    }
}