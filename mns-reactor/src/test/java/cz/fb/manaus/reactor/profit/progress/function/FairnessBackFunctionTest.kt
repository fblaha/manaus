package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.realizedBet
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class FairnessBackFunctionTest {

    private val fc = FairnessBackFunction

    @Test
    fun `get value`() {
        assertTrue(fc(realizedBet)!! in (0.5..1.0))
    }
}