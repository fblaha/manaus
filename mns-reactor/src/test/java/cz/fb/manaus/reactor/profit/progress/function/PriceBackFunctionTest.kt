package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.realizedBet
import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class PriceBackFunctionTest : AbstractLocalTestCase() {
    @Autowired
    private lateinit var fc: PriceBackFunction

    @Test
    fun `get value`() {
        assertEquals(3.3, fc(realizedBet))
    }

}