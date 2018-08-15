package cz.fb.manaus.core.manager.filter

import cz.fb.manaus.core.model.EventType
import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.core.test.CoreTestFactory
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CategoryBlacklistFilterTest : AbstractLocalTestCase() {
    @Autowired
    private lateinit var filter: CategoryBlacklistFilter
    private lateinit var market: Market
    private lateinit var eventType: EventType

    @Before
    fun setUp() {
        market = CoreTestFactory.newMarket()
        eventType = EventType("1", "Soccer")
        market.eventType = eventType
    }

    @Test
    fun testFilter() {
        assertTrue(filter.accept(market, setOf()))
        assertFalse(filter.accept(market, setOf("market_sport_soccer")))
    }
}


