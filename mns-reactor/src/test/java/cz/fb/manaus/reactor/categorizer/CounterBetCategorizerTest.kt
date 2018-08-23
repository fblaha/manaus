package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.core.test.CoreTestFactory.newSettledBet
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class CounterBetCategorizerTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var categorizer: CounterBetCategorizer

    @Test
    fun `counter - equal prices`() {
        val coverage = BetCoverage.from(listOf(newSettledBet(2.0, Side.BACK)))
        assertEquals(setOf("counter_zero"), categorizer.getCategories(newSettledBet(2.0, Side.LAY), coverage))
    }

    @Test
    fun `counter - sure profit`() {
        val coverage = BetCoverage.from(listOf(newSettledBet(2.5, Side.BACK)))
        assertEquals(setOf("counter_profit"), categorizer.getCategories(newSettledBet(2.0, Side.LAY), coverage))
    }

    @Test
    fun `category - sure loss`() {
        val coverage = BetCoverage.from(listOf(newSettledBet(1.5, Side.BACK)))
        assertEquals(setOf("counter_loss"), categorizer.getCategories(newSettledBet(2.0, Side.LAY), coverage))
    }

    @Test
    fun `no counter bet`() {
        val coverage = BetCoverage.from(listOf(newSettledBet(2.0, Side.LAY)))
        assertEquals(setOf("counter_none"), categorizer.getCategories(newSettledBet(2.0, Side.LAY), coverage))
    }

}