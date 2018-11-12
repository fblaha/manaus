package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.model.realizedBet
import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class ReciprocalCategorizerTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var categorizer: ReciprocalCategorizer

    @Test
    fun category() {
        assertEquals(setOf("reciprocal_0.80-0.85"), categorizer.getCategories(realizedBet, BetCoverage.EMPTY))
    }

}