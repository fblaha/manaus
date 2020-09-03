package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.realizedBet
import cz.fb.manaus.core.test.AbstractTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertNull

class PriceLayFunctionTest : AbstractTestCase() {
    @Autowired
    private lateinit var fc: PriceLayFunction

    @Test
    fun `get value`() {
        assertNull(fc(realizedBet))
    }
}