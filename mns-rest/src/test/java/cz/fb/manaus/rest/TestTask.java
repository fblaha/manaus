package cz.fb.manaus.rest;

import cz.fb.manaus.core.maintanance.ConfigUpdate;
import cz.fb.manaus.core.maintanance.PeriodicMaintenanceTask;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
class TestTask implements PeriodicMaintenanceTask {

    @Override
    public String getName() {
        return "testTask";
    }

    @Override
    public Duration getPausePeriod() {
        return Duration.ofNanos(777);
    }

    @Override
    public ConfigUpdate execute() {
        var command = ConfigUpdate.empty(Duration.ofHours(12));
        command.getDeletePrefixes().add("test_delete");
        return command;
    }
}
