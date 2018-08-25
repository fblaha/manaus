package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class PriceCategorizerTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var categorizer: PriceCategorizer

    @Test
    fun `get categories`() {
        assertEquals(categorizer.getCategory(1.5), "priceRange_1.5-2.0")
        assertEquals(categorizer.getCategory(1.8), "priceRange_1.5-2.0")
        assertEquals(categorizer.getCategory(1.01), "priceRange_1.0-1.2")
        assertEquals(categorizer.getCategory(2.0), "priceRange_2.0-2.5")
        assertEquals(categorizer.getCategory(2.3), "priceRange_2.0-2.5")
        assertEquals(categorizer.getCategory(2.5), "priceRange_2.5-3.0")
        assertEquals(categorizer.getCategory(2.9), "priceRange_2.5-3.0")
        assertEquals(categorizer.getCategory(4.4), "priceRange_4.0-5.0")
        assertEquals(categorizer.getCategory(5.5), "priceRange_5.0+")
    }
}
