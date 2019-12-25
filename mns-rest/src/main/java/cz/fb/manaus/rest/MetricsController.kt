package cz.fb.manaus.rest

import com.codahale.metrics.Histogram
import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.SlidingWindowReservoir
import cz.fb.manaus.core.metrics.MetricRecord
import cz.fb.manaus.core.metrics.MetricsService
import cz.fb.manaus.spring.ManausProfiles
import io.micrometer.core.instrument.Metrics
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@Profile(ManausProfiles.DB)
class MetricsController(private val metricsService: MetricsService,
                        private val metricRegistry: MetricRegistry) {


    val metrics: List<MetricRecord<*>>
        @ResponseBody
        @RequestMapping(value = ["/metrics"], method = [RequestMethod.GET])
        get() = getMetrics("")

    @ResponseBody
    @RequestMapping(value = ["/metrics/{prefix}"], method = [RequestMethod.GET])
    fun getMetrics(@PathVariable prefix: String): List<MetricRecord<*>> {
        metricRegistry.counter("metric.get").inc()
        Metrics.counter("metric_get").increment()
        return metricsService.getCollectedMetrics(prefix)
    }

    @ResponseBody
    @RequestMapping(value = ["/metrics/histogram/{name}"], method = [RequestMethod.POST])
    fun updateHistogram(@PathVariable name: String,
                        @RequestParam(defaultValue = "100") reservoirSize: Int,
                        @RequestBody value: Long) {
        metricRegistry.histogram(name) { Histogram(SlidingWindowReservoir(reservoirSize)) }
                .update(value)
    }

    @ResponseBody
    @RequestMapping(value = ["/metrics/meter/{name}"], method = [RequestMethod.POST])
    fun updateMeter(@PathVariable name: String, @RequestBody value: Long) {
        metricRegistry.meter(name).mark(value)
    }

    @ResponseBody
    @RequestMapping(value = ["/metrics/counter/{name}"], method = [RequestMethod.POST])
    fun updateCounter(@PathVariable name: String, @RequestBody value: Long) {
        metricRegistry.counter(name).inc(value)
    }
}
