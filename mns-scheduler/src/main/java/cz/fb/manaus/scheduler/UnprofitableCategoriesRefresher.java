package cz.fb.manaus.scheduler;

import cz.fb.manaus.core.maintanance.PeriodicMaintenanceTask;
import cz.fb.manaus.reactor.filter.AbstractUnprofitableCategoriesRegistry;
import cz.fb.manaus.spring.DatabaseComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;

import static org.apache.commons.lang3.time.DateUtils.MILLIS_PER_MINUTE;

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
        return Duration.ofMinutes(10);
    }

    @Override
    public void run() {
        updateUnprofitableCategoriesRegistry();
    }

    @Scheduled(fixedDelay = 10 * MILLIS_PER_MINUTE)
    public void updateUnprofitableCategoriesRegistry() {
        unprofitableCategoriesRegistries.forEach(AbstractUnprofitableCategoriesRegistry::updateBlackLists);
    }
}
