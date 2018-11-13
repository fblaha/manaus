package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.model.realizedBet
import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class ActualMatchedCategorizerTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var categorizer: ActualMatchedCategorizer

    @Test
    fun category() {
        assertEquals(setOf("actualMatchedMarket_100-1k"),
                categorizer.getCategories(realizedBet, BetCoverage.EMPTY))
    }

}