package cz.fb.manaus.core.manager.filter

import cz.fb.manaus.core.repository.domain.marketTemplate
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
        assertTrue(filter.accept(marketTemplate, setOf()))
        assertFalse(filter.accept(marketTemplate, setOf("market_sport_soccer")))
    }
}


