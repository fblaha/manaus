package cz.fb.manaus.core.metrics

import com.codahale.metrics.MetricRegistry
import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class MetricsServiceTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var service: MetricsService
    @Autowired
    private lateinit var registry: MetricRegistry

    private val recordMap: Map<String, MetricRecord<*>>
        get() {
            val collectedMetrics = service!!.getCollectedMetrics("test")
            return collectedMetrics.map { it.name to it }.toMap()
        }

    @Test
    fun `counter inc`() {
        registry.counter("test.counter").inc()
        assertEquals(1L, recordMap["test.counter"]?.value)
    }

    @Test
    fun `meter value`() {
        registry.meter("test.meter").mark(10)
        registry.meter("test.meter").mark(20)
        assertEquals(30L, recordMap["test.meter.count"]?.value)
    }

    @Test
    fun `histogram values`() {
        registry.histogram("test.hist").update(10)
        registry.histogram("test.hist").update(20)
        assertEquals(20L, recordMap["test.hist.max"]?.value)
        assertEquals(10L, recordMap["test.hist.min"]?.value)
    }

}