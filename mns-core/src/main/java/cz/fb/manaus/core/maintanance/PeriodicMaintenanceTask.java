package cz.fb.manaus.core.maintanance;

import java.time.Duration;

public interface PeriodicMaintenanceTask {

    String getName();

    Duration getPausePeriod();

    void run();
}
