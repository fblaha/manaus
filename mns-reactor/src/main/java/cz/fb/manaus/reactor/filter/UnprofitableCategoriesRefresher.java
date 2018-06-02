package cz.fb.manaus.reactor.filter;

import cz.fb.manaus.core.maintanance.ConfigUpdate;
import cz.fb.manaus.core.maintanance.PeriodicMaintenanceTask;
import cz.fb.manaus.spring.ManausProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;

@Component
@Profile(ManausProfiles.DB)
public class UnprofitableCategoriesRefresher implements PeriodicMaintenanceTask {

    public static final String REFRESH_PERIOD_EL = "#{systemEnvironment['MNS_UNPROFITABLE_REFRESH_PERIOD_HRS'] ?: 8}";
    private final int refreshPeriodHours;

    @Autowired
    public UnprofitableCategoriesRefresher(@Value(REFRESH_PERIOD_EL) int refreshPeriodHours) {
        this.refreshPeriodHours = refreshPeriodHours;
    }

    @Autowired(required = false)
    private List<AbstractUnprofitableCategoriesRegistry> unprofitableCategoriesRegistries = new LinkedList<>();

    @Override
    public String getName() {
        return "unprofitableCategoriesRefresh";
    }

    @Override
    public Duration getPausePeriod() {
        return Duration.ofHours(8);
    }

    @Override
    public ConfigUpdate execute() {
        var configUpdate = ConfigUpdate.empty(Duration.ofDays(1));
        for (var registry : unprofitableCategoriesRegistries) {
            registry.updateBlacklists(configUpdate);
        }
        return configUpdate;
    }
}
