package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.model.realizedBet
import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class LayPriceCountCategorizerTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var categorizer: LayPriceCountCategorizer

    @Test
    fun category() {
        assertEquals(setOf("priceCountLay_2"), categorizer.getCategories(realizedBet, BetCoverage.EMPTY))
    }
}