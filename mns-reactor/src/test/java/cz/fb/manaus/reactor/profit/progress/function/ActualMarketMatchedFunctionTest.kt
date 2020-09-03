package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.realizedBet
import cz.fb.manaus.core.test.AbstractTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class ActualMarketMatchedFunctionTest : AbstractTestCase() {
    @Autowired
    private lateinit var fc: ActualMarketMatchedFunction

    @Test
    fun `get value`() {
        assertEquals(300.0, fc(realizedBet))
    }

}