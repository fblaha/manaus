package cz.fb.manaus.rest

import com.codahale.metrics.MetricRegistry
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.assertEquals


class MetricsControllerTest : AbstractControllerTest() {

    @Autowired
    private lateinit var metricRegistry: MetricRegistry

    @Test
    fun testMetrics() {
        checkResponse("/metrics", "metric.get", "1")
    }

    @Test
    fun `update histogram`() {
        mvc.perform(post(
                "/metrics/histogram/{name}", "histogram.test")
                .contentType(MediaType.APPLICATION_JSON)
                .content("100"))
                .andExpect(status().isOk)
                .andReturn()
        assertEquals(1, metricRegistry.histogram("histogram.test").count)
    }

    @Test
    fun `update meter`() {
        mvc.perform(post(
                "/metrics/meter/{name}", "meter.test")
                .contentType(MediaType.APPLICATION_JSON)
                .content("100"))
                .andExpect(status().isOk)
                .andReturn()
        assertEquals(100, metricRegistry.meter("meter.test").count)
    }

    @Test
    fun `update counter`() {
        mvc.perform(post(
                "/metrics/counter/{name}", "counter.test")
                .contentType(MediaType.APPLICATION_JSON)
                .content("100"))
                .andExpect(status().isOk)
                .andReturn()
        assertEquals(100, metricRegistry.counter("counter.test").count)
    }
}