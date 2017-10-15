package cz.fb.manaus.rest;

import cz.fb.manaus.core.metrics.MetricRecord;
import cz.fb.manaus.core.metrics.MetricsContributor;
import cz.fb.manaus.core.metrics.MetricsManager;
import cz.fb.manaus.core.metrics.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Stream;

@Controller
public class MetricsController implements MetricsContributor {

    @Autowired
    private MetricsService metricsService;
    @Autowired
    private MetricsManager metricsManager;

    @ResponseBody
    @RequestMapping(value = "/metrics", method = RequestMethod.GET)
    public List<MetricRecord<?>> getMetrics() {
        return getMetrics("");
    }

    @ResponseBody
    @RequestMapping(value = "/metrics/{prefix}", method = RequestMethod.GET)
    public List<MetricRecord<?>> getMetrics(@PathVariable String prefix) {
        metricsManager.getRegistry().counter("metrics").inc();
        return metricsService.getCollectedMetrics(prefix);
    }

    @Override
    public Stream<MetricRecord<?>> getMetricRecords() {
        return metricsManager.getCounterMetricRecords("metrics");
    }
}
