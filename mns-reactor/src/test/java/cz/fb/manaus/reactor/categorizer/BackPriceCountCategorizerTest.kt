package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.core.test.CoreTestFactory
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class BackPriceCountCategorizerTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var categorizer: BackPriceCountCategorizer

    @Test
    fun category() {
        assertEquals(setOf("priceCountBack_3+"),
                categorizer.getCategories(CoreTestFactory.newSettledBet(2.0, Side.LAY), BetCoverage.EMPTY))
    }
}