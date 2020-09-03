package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.MarketCategories
import cz.fb.manaus.core.model.realizedBet
import cz.fb.manaus.core.test.AbstractTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class AllCategorizerTest : AbstractTestCase() {

    @Autowired
    private lateinit var categorizer: AllCategorizer

    @Test
    fun category() {
        assertEquals(setOf(MarketCategories.ALL), categorizer.getCategories(realizedBet))
    }

}