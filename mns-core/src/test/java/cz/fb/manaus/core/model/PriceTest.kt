package cz.fb.manaus.core.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class PriceTest {

    @Test
    fun testEq() {
        assertEquals(Price(2.28, 2.24, Side.LAY), Price(2.28, 2.24, Side.LAY))
        assertNotEquals(Price(2.28, 2.04, Side.LAY), Price(2.28, 2.24, Side.LAY))
    }

    @Test
    fun testCmp() {
        assertTrue { Price(2.0, 2.0, Side.BACK) < Price(3.0, 2.0, Side.BACK) }
        assertTrue { Price(2.0, 2.0, Side.LAY) > Price(3.0, 2.0, Side.LAY) }
    }

    @ParameterizedTest
    @CsvSource(
        "2, 2",
        "1.99999999, 2",
        "3.0000001, 3",
        "3.01, 3.01"
    )
    fun round(input: Double, expected: Double) {
        assertEquals(expected, Price.round(input))
    }

    @ParameterizedTest
    @CsvSource(
        "2, 2, true",
        "2, 1.99999999, true",
        "2, 2.00000001, true",
        "2, 2.1, false",
        "2, 3, false",
    )
    fun priceEq(p1: Double, p2: Double, expected: Boolean) {
        assertEquals(expected, p1 priceEq p2)
    }

}
