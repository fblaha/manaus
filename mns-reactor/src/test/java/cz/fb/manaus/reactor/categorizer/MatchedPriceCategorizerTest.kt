package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.model.Side
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MatchedPriceCategorizerTest {

    private val categorizer = MatchedPriceCategorizer

    @Test
    fun `better category`() {
        assertEquals("matchedPrice_better", categorizer.getCategory(3.5, 3.0, Side.BACK))
        assertEquals("matchedPrice_better", categorizer.getCategory(2.5, 3.0, Side.LAY))
    }

    @Test
    fun `equal category`() {
        assertEquals("matchedPrice_equal", categorizer.getCategory(3.0, 3.0, Side.BACK))
        assertEquals("matchedPrice_equal", categorizer.getCategory(3.0, 3.0, Side.LAY))
    }

    @Test
    fun `worse category`() {
        assertEquals("matchedPrice_worse", categorizer.getCategory(2.5, 3.0, Side.BACK))
        assertEquals("matchedPrice_worse", categorizer.getCategory(3.5, 3.0, Side.LAY))
    }

}