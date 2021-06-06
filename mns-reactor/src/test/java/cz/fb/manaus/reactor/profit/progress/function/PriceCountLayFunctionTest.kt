package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.realizedBet
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PriceCountLayFunctionTest {

    @Test
    fun category() {
        assertEquals(2.0, PriceCountLayFunction(realizedBet))
    }
}