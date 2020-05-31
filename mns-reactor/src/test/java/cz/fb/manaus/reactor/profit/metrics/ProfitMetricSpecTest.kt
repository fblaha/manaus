package cz.fb.manaus.reactor.profit.metrics

import cz.fb.manaus.core.model.ProfitRecord
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ProfitMetricSpecTest {

    private val spec = ProfitMetricSpec(
            interval = "24h",
            categoryPrefix = "market_type",
            categoryValues = setOf("match_odds", "total")
    )

    @Test
    fun `metric name`() {
        assertEquals("mns_profit_market_type_24h", spec.metricName)
    }

    @Test
    fun `extract category value`() {
        assertEquals("total", spec.extractVal("market_type_total"))
    }

    @Test
    fun `record predicate`() {
        val profitRecord = ProfitRecord("market_type_total", 5.0, 2.0, 0.2, 2, 1)
        assertTrue { spec.recordPredicate(profitRecord) }
        assertFalse { spec.recordPredicate(profitRecord.copy(category = "other")) }
    }
}