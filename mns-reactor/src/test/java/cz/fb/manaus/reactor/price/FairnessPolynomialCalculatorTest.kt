package cz.fb.manaus.reactor.price

import cz.fb.manaus.core.model.priceEq
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class FairnessPolynomialCalculatorTest {

    private val eps = 0.001
    private val calculator = FairnessPolynomialCalculator

    @Test
    fun `polynomial fairness`() {
        val calculator = FairnessPolynomialCalculator
        assertEquals(0.866, calculator.getFairness(1, listOf(2.5, 1.5))!!, eps)
        assertEquals(0.825, calculator.getFairness(1, listOf(2.7, 1.4))!!, eps)
        assertEquals(0.75, calculator.getFairness(1, listOf(2.5, 2.5, 2.5))!!, eps)
        assertEquals(0.85, calculator.getFairness(1, listOf(2.7, 2.7, 2.7))!!, eps)
    }

    @Test
    fun `fairness - complex case`() {
        val fairness = calculator.getFairness(1, BEST_PRICES_HARD)!!
        assertTrue { fairness > 0 }
    }

    @Test
    fun `fairness 1 winner`() {
        assertTrue { 1.0 priceEq calculator.getFairness(1, listOf(3.0, 3.0, 3.0))!! }
    }

    @Test
    fun `fairness 2 winners`() {
        assertEquals(1.0, calculator.getFairness(2, listOf(1.5, 1.5, 1.5))!!, 0.0001)
    }

    @Test
    fun `fairness 2 winners - comparison`() {
        assertTrue {
            calculator.getFairness(2, listOf(1.4, 1.5, 1.5))!! <
                    calculator.getFairness(2, listOf(1.5, 1.5, 1.5))!!
        }
    }

    @Test
    fun `fairness lay 2 runners`() {
        assertEquals(1.118, calculator.getFairness(1, listOf(1.5, 3.5))!!, eps)
    }

    @Test
    fun `fairness lay 3 runners`() {
        assertEquals(1.093, calculator.getFairness(1, listOf(3.5, 3.5, 2.7))!!, eps)
    }


    companion object {
        val BEST_PRICES_HARD = listOf(
            85.0, 510.0, 270.0, 700.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0,
            1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0,
            38.0, 95.0, 110.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0,
            1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0
        )
    }

}