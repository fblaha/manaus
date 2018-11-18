package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.realizedBet
import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class ActualSelectionMatchedFunctionTest : AbstractLocalTestCase() {
    @Autowired
    private lateinit var fc: ActualSelectionMatchedFunction

    @Test
    fun `get value`() {
        assertEquals(100.0, fc(realizedBet))
    }

}