package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.realizedBet
import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class PriceCountBackFunctionTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var fc: PriceCountBackFunction

    @Test
    fun category() {
        assertEquals(2.0, fc(realizedBet))
    }
}