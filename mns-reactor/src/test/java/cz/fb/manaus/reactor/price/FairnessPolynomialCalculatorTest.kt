package cz.fb.manaus.reactor.price

import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.*
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired


class FairnessPolynomialCalculatorTest : AbstractLocalTestCase() {


    @Autowired
    private lateinit var calculator: FairnessPolynomialCalculator

    @Test
    fun `polynomial fairness`() {
        assertEquals(0.866, calculator.getFairness(1.0, listOf(2.5, 1.5))!!, 0.001)
        assertEquals(0.825, calculator.getFairness(1.0, listOf(2.7, 1.4))!!, 0.001)
        assertEquals(0.75, calculator.getFairness(1.0, listOf(2.5, 2.5, 2.5))!!, 0.001)
        assertEquals(0.85, calculator.getFairness(1.0, listOf(2.7, 2.7, 2.7))!!, 0.001)
    }

    @Test
    fun `fairness - complex case`() {
        val fairness = calculator.getFairness(1.0, BEST_PRICES_HARD)!!
        assertTrue(fairness > 0)
    }

    @Test
    fun `fairness 1 winner`() {
        assertThat(calculator.getFairness(1.0, listOf(3.0, 3.0, 3.0))!!, `is`(1.0))
    }

    @Test
    fun `fairness 2 winners`() {
        assertEquals(1.0, calculator.getFairness(2.0,
                listOf(1.5, 1.5, 1.5))!!, 0.0001)
    }

    @Test
    fun `fairness 2 winners - comparison`() {
        assertTrue(calculator.getFairness(2.0, listOf(1.4, 1.5, 1.5))!! <
                calculator.getFairness(2.0, listOf(1.5, 1.5, 1.5))!!)
    }

    @Test
    fun `fairness lay 2 runners`() {
        assertEquals(1.118, calculator.getFairness(1.0, listOf(1.5, 3.5))!!, 0.001)
    }

    @Test
    fun `fairness lay 3 runners`() {
        assertEquals(1.093, calculator.getFairness(1.0, listOf(3.5, 3.5, 2.7))!!, 0.001)
    }

    companion object {
        val BEST_PRICES_HARD = listOf(
                85.0, 510.0, 270.0, 700.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0,
                1000.0, 1000.0, 1000.0, 1000.0, 38.0, 95.0, 110.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0,
                1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0)
    }

}