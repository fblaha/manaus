package cz.fb.manaus.rest;

import com.codahale.metrics.MetricRegistry;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class MetricsControllerTest extends AbstractControllerTest {

    @Autowired
    private MetricRegistry metricRegistry;

    @Test
    public void testMetrics() throws Exception {
        checkResponse("/metrics", "metric.get", "1");
    }

    @Test
    public void testUpdateHistogram() throws Exception {
        mvc.perform(post(
                "/metrics/histogram/{name}", "histogram.test")
                .contentType(MediaType.APPLICATION_JSON)
                .content("100"))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(metricRegistry.histogram("histogram.test").getCount(), is(1L));
    }

    @Test
    public void testUpdateMeter() throws Exception {
        mvc.perform(post(
                "/metrics/meter/{name}", "meter.test")
                .contentType(MediaType.APPLICATION_JSON)
                .content("100"))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(metricRegistry.meter("meter.test").getCount(), is(100L));
    }

    @Test
    public void testUpdateCounter() throws Exception {
        mvc.perform(post(
                "/metrics/counter/{name}", "counter.test")
                .contentType(MediaType.APPLICATION_JSON)
                .content("100"))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(metricRegistry.counter("counter.test").getCount(), is(100L));
    }
}