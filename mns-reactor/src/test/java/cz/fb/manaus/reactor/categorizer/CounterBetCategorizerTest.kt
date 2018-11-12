package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.model.realizedBet
import cz.fb.manaus.core.model.replacePrice
import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class CounterBetCategorizerTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var categorizer: CounterBetCategorizer

    @Test
    fun `counter - equal prices`() {
        val back = realizedBet.replacePrice(Price(2.0, 2.0, Side.BACK))
        val lay = realizedBet.replacePrice(Price(2.0, 2.0, Side.LAY))
        val coverage = BetCoverage.from(listOf(back))
        assertEquals(setOf("counter_zero"), categorizer.getCategories(lay, coverage))
    }

    @Test
    fun `counter - sure profit`() {
        val back = realizedBet.replacePrice(Price(2.5, 2.0, Side.BACK))
        val lay = realizedBet.replacePrice(Price(2.0, 2.0, Side.LAY))
        val coverage = BetCoverage.from(listOf(back))
        assertEquals(setOf("counter_profit"), categorizer.getCategories(lay, coverage))
    }

    @Test
    fun `category - sure loss`() {
        val back = realizedBet.replacePrice(Price(1.5, 2.0, Side.BACK))
        val lay = realizedBet.replacePrice(Price(2.0, 2.0, Side.LAY))
        val coverage = BetCoverage.from(listOf(back))
        assertEquals(setOf("counter_loss"), categorizer.getCategories(lay, coverage))
    }

    @Test
    fun `no counter bet`() {
        val lay = realizedBet.replacePrice(Price(2.0, 2.0, Side.LAY))
        val coverage = BetCoverage.from(listOf(lay))
        assertEquals(setOf("counter_none"), categorizer.getCategories(lay, coverage))
    }

}