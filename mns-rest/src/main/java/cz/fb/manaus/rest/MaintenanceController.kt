package cz.fb.manaus.rest

import com.codahale.metrics.MetricRegistry
import cz.fb.manaus.core.maintanance.ConfigUpdate
import cz.fb.manaus.core.maintanance.PeriodicMaintenanceTask
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@Profile(ManausProfiles.DB)
class MaintenanceController {

    @Autowired
    private val metricRegistry: MetricRegistry? = null

    @Autowired(required = false)
    @get:ResponseBody
    @get:RequestMapping(value = ["/maintenance"], method = [RequestMethod.GET])
    val tasks: List<PeriodicMaintenanceTask> = mutableListOf()

    @ResponseBody
    @RequestMapping(value = ["/maintenance/{name}"], method = [RequestMethod.POST])
    fun runTask(@PathVariable name: String): ResponseEntity<*> {
        metricRegistry!!.counter("maintenance.$name").inc()
        val task = tasks.find { t -> name == t.name }
        val update = task?.execute()
        return if (update == null) {
            ResponseEntity.notFound().build<ConfigUpdate>()
        } else {
            ResponseEntity.ok(update)
        }
    }
}