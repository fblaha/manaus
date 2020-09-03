package cz.fb.manaus.reactor.betting.listener

import cz.fb.manaus.core.model.RunnerPrices
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.AbstractTestCase
import cz.fb.manaus.reactor.PricesTestFactory
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class ProbabilityComparatorTest : AbstractTestCase() {

    @Autowired
    private lateinit var factory: PricesTestFactory

    @Test
    fun `probability comparison`() {
        val first = factory.newRunnerPrices(1, 1.4, 1.6)
        val second = factory.newRunnerPrices(2, 2.8, 3.3)
        assertEquals(1L, getFirstSelection(listOf(first, first)))
        assertEquals(1L, getFirstSelection(listOf(first, second)))
        assertEquals(1L, getFirstSelection(listOf(second, first)))
        assertEquals(2L, getFirstSelection(listOf(second, second)))
    }

    private fun getFirstSelection(lists: List<RunnerPrices>): Long {
        return lists.sortedWith(ProbabilityComparator.COMPARATORS[Side.BACK]!!)[0].selectionId
    }
}