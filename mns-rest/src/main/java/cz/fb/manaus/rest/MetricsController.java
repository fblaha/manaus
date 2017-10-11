package cz.fb.manaus.rest;

import cz.fb.manaus.core.metrics.MetricRecord;
import cz.fb.manaus.core.metrics.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class MetricsController {

    @Autowired
    private MetricsService metricsService;

    @ResponseBody
    @RequestMapping(value = "/metrics", method = RequestMethod.GET)
    public List<MetricRecord> getMetrics() {
        return getMetrics("");
    }

    @ResponseBody
    @RequestMapping(value = "/metrics/{prefix}", method = RequestMethod.GET)
    public List<MetricRecord> getMetrics(@PathVariable String prefix) {
        return metricsService.getCollectedMetrics(prefix);
    }

}
