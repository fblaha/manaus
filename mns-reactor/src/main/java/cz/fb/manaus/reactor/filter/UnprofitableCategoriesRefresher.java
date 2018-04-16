package cz.fb.manaus.reactor.filter;

import cz.fb.manaus.core.maintanance.ConfigUpdate;
import cz.fb.manaus.core.maintanance.PeriodicMaintenanceTask;
import cz.fb.manaus.spring.DatabaseComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;

@DatabaseComponent
public class UnprofitableCategoriesRefresher implements PeriodicMaintenanceTask {

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
        ConfigUpdate configUpdate = ConfigUpdate.empty(Duration.ofDays(1));
        for (AbstractUnprofitableCategoriesRegistry registry : unprofitableCategoriesRegistries) {
            registry.updateBlackLists(configUpdate);
        }
        return ConfigUpdate.empty(Duration.ZERO);
    }
}
