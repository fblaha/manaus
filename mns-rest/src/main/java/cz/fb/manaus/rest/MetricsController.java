package cz.fb.manaus.rest;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SlidingWindowReservoir;
import cz.fb.manaus.core.metrics.MetricRecord;
import cz.fb.manaus.core.metrics.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class MetricsController {

    @Autowired
    private MetricsService metricsService;
    @Autowired
    private MetricRegistry metricRegistry;

    @ResponseBody
    @RequestMapping(value = "/metrics", method = RequestMethod.GET)
    public List<MetricRecord<?>> getMetrics() {
        return getMetrics("");
    }

    @ResponseBody
    @RequestMapping(value = "/metrics/{prefix}", method = RequestMethod.GET)
    public List<MetricRecord<?>> getMetrics(@PathVariable String prefix) {
        metricRegistry.counter("metric.get").inc();
        return metricsService.getCollectedMetrics(prefix);
    }

    @ResponseBody
    @RequestMapping(value = "/metrics/histogram/{name}", method = RequestMethod.POST)
    public void updateHistogram(@PathVariable String name,
                                @RequestParam(defaultValue = "100") int reservoirSize,
                                @RequestBody long value) {
        metricRegistry.histogram(name,
                () -> new Histogram(new SlidingWindowReservoir(reservoirSize)))
                .update(value);
    }

    @ResponseBody
    @RequestMapping(value = "/metrics/meter/{name}", method = RequestMethod.POST)
    public void updateMeter(@PathVariable String name, @RequestBody long value) {
        metricRegistry.meter(name).mark(value);
    }

    @ResponseBody
    @RequestMapping(value = "/metrics/counter/{name}", method = RequestMethod.POST)
    public void updateCounter(@PathVariable String name, @RequestBody long value) {
        metricRegistry.counter(name).inc(value);
    }
}
