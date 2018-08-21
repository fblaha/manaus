package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.core.test.CoreTestFactory
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class LayPriceCountCategorizerTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var categorizer: LayPriceCountCategorizer

    @Test
    fun category() {
        assertEquals(setOf("priceCountLay_0"), categorizer.getCategories(CoreTestFactory.newSettledBet(2.0, Side.LAY), BetCoverage.EMPTY))
    }
}