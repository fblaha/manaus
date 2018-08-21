package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*
import kotlin.test.assertEquals


class TheAbstractBeforeCategorizerTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var categorizer: TestBeforeCategorizer

    @Test
    fun testBeforeResolver() {
        val cat = categorizer.dayMap.get(2L)
        assertEquals("test_day_2-3", cat)
    }

    @Component
    private class TestBeforeCategorizer : AbstractBeforeCategorizer("test") {

        override fun getDate(settledBet: SettledBet): Date {
            return Date()
        }

    }

}