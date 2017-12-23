package cz.fb.manaus.rest;

import com.codahale.metrics.MetricRegistry;
import cz.fb.manaus.core.maintanance.PeriodicMaintenanceTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Controller
public class MaintenanceController {

    @Autowired
    private MetricRegistry metricRegistry;

    @Autowired(required = false)
    private List<PeriodicMaintenanceTask> maintenanceTasks = new LinkedList<>();

    @ResponseBody
    @RequestMapping(value = "/maintenance", method = RequestMethod.GET)
    public List<PeriodicMaintenanceTask> getTasks() {
        return maintenanceTasks;
    }

    @ResponseBody
    @RequestMapping(value = "/maintenance/{name}", method = RequestMethod.POST)
    public ResponseEntity<?> runTask(@PathVariable String name) {
        metricRegistry.counter("maintenance." + name).inc();
        Optional<PeriodicMaintenanceTask> task = maintenanceTasks.stream()
                .filter(t -> name.equals(t.getName())).findAny();
        task.ifPresent(PeriodicMaintenanceTask::run);
        return task.map(t -> ResponseEntity.ok().build())
                .orElse(ResponseEntity.notFound().build());
    }
}
