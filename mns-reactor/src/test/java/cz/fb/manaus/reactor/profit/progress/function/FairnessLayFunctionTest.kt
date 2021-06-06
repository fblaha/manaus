package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.realizedBet
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class FairnessLayFunctionTest {

    @Test
    fun `get value`() {
        assertTrue { FairnessLayFunction(realizedBet)!! in (1.0..1.5) }
    }
}