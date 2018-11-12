package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.model.realizedBet
import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class BackPriceCountCategorizerTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var categorizer: BackPriceCountCategorizer

    @Test
    fun category() {
        assertEquals(setOf("priceCountBack_3+"),
                categorizer.getCategories(realizedBet, BetCoverage.EMPTY))
    }
}