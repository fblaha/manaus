package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.realizedBet
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PriceBackFunctionTest {

    @Test
    fun `get value`() {
        assertEquals(3.3, PriceBackFunction(realizedBet))
    }

}