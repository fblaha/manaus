package cz.fb.manaus.core.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class PriceTest {

    @Test
    fun testEq() {
        assertEquals(Price(2.28, 2.24, Side.LAY), Price(2.28, 2.24, Side.LAY))
        assertNotEquals(Price(2.28, 2.04, Side.LAY), Price(2.28, 2.24, Side.LAY))
    }
}
