package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.MarketCategories
import cz.fb.manaus.core.model.realizedBet
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AllCategorizerTest {

    @Test
    fun category() {
        assertEquals(setOf(MarketCategories.ALL), AllCategorizer.getCategories(realizedBet))
    }

}