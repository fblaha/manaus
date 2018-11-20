package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.realizedBet
import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertTrue

class FairnessLayFunctionTest : AbstractLocalTestCase() {
    @Autowired
    private lateinit var fc: FairnessLayFunction

    @Test
    fun `get value`() {
        assertTrue(fc(realizedBet)!! in (1.0..1.5))
    }
}