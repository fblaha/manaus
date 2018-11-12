package cz.fb.manaus.core.manager.filter

import cz.fb.manaus.core.model.market
import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CategoryBlacklistFilterTest : AbstractLocalTestCase() {
    @Autowired
    private lateinit var filter: CategoryBlacklistFilter


    @Test
    fun testFilter() {
        assertTrue(filter.accept(market, setOf()))
        assertFalse(filter.accept(market, setOf("market_sport_soccer")))
    }
}


