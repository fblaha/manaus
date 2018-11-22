package cz.fb.manaus.core.manager

import junit.framework.TestCase.assertTrue
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class MarketFilterServiceTest : AbstractMarketDataAwareTestCase() {
    @Autowired
    private lateinit var filterService: MarketFilterService

    @Test
    fun filtering() {
        checkFilterCount(1000..1500, false)
        checkFilterCount(6700..6700, true)
    }

    private fun checkFilterCount(expectedRange: ClosedRange<Int>, hasBets: Boolean) {
        val cnt = markets.filter { filterService.accept(it, hasBets, setOf()) }
                .count()
        assertTrue(cnt in expectedRange)
    }
}
