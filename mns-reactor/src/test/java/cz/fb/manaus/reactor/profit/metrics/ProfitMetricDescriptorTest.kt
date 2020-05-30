package cz.fb.manaus.reactor.profit.metrics

import org.junit.Test
import kotlin.test.assertEquals

class ProfitMetricDescriptorTest {

    private val spec = ProfitMetricDescriptor(
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

}