package cz.fb.manaus.core.maintanance;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Duration;

public interface PeriodicMaintenanceTask {

    ConfigUpdate execute();

    String getName();

    @JsonIgnore
    Duration getPausePeriod();

    default long getPausePeriodNanos() {
        return getPausePeriod().toNanos();
    }

}
