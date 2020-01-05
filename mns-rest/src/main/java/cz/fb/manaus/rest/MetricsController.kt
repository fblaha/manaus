package cz.fb.manaus.rest

import cz.fb.manaus.core.metrics.MetricRecord
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

// TODO remove
@Controller
@Profile(ManausProfiles.DB)
class MetricsController {

    @ResponseBody
    @RequestMapping(value = ["/metrics/{prefix}"], method = [RequestMethod.GET])
    fun getMetrics(@PathVariable prefix: String): List<MetricRecord<*>> {
        return emptyList()
    }

}
