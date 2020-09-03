package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.realizedBet
import cz.fb.manaus.core.test.AbstractTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class ActualRunnerMatchedFunctionTest : AbstractTestCase() {
    @Autowired
    private lateinit var fc: ActualRunnerMatchedFunction

    @Test
    fun `get value`() {
        assertEquals(100.0, fc(realizedBet))
    }

}