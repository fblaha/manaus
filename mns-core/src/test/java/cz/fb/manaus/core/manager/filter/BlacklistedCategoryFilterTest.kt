package cz.fb.manaus.core.manager.filter

import cz.fb.manaus.core.model.BlacklistedCategory
import cz.fb.manaus.core.model.market
import cz.fb.manaus.core.test.AbstractDatabaseTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BlacklistedCategoryFilterTest : AbstractDatabaseTestCase() {
    @Autowired
    private lateinit var filter: BlacklistedCategoryFilter

    @Test
    fun testFilter() {
        assertTrue(filter.accept(market))
        val soccer = "market_sport_soccer"
        blacklistedCategoryRepository.saveOrUpdate(BlacklistedCategory(soccer, Duration.ofDays(10), -30.0))
        assertFalse(filter.accept(market))
    }
}


