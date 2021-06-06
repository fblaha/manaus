package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.realizedBet
import org.junit.jupiter.api.Test
import kotlin.test.assertNull

class PriceLayFunctionTest {

    @Test
    fun `get value`() {
        assertNull(PriceLayFunction(realizedBet))
    }
}