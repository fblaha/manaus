package cz.fb.manaus.scheduler;


import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Ordering;
import cz.fb.manaus.core.provider.ExchangeProvider;
import cz.fb.manaus.core.provider.ProviderConfigurationValidator;
import cz.fb.manaus.core.provider.ProviderTask;
import cz.fb.manaus.core.service.PeriodicTaskService;
import cz.fb.manaus.spring.DatabaseComponent;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@DatabaseComponent
public class ProviderTaskScheduler {

    public static final Ordering<ProviderTask> TASK_ORDERING = Ordering
            .from(Comparator.comparing(ProviderTask::getPauseDuration));

    private static final Logger log = Logger.getLogger(ProviderTaskScheduler.class.getSimpleName());
    private final List<ProviderTask> tasks;
    @Autowired
    private PeriodicTaskService taskService;
    @Autowired
    private Optional<ProviderConfigurationValidator> validator;
    @Autowired
    private ExchangeProvider provider;


    @Autowired
    public ProviderTaskScheduler(List<ProviderTask> tasks) {
        long distinctCount = tasks.stream().map(ProviderTask::getName).distinct().count();
        long count = tasks.stream().map(ProviderTask::getName).count();
        Preconditions.checkState(count == distinctCount);
        this.tasks = TASK_ORDERING.immutableSortedCopy(tasks);
    }

    @Scheduled(fixedDelay = 5 * DateUtils.MILLIS_PER_SECOND, initialDelay = DateUtils.MILLIS_PER_SECOND)
    public void executeTasks() {
        if (validator.isPresent() && validator.get().isConfigured()) {
            tasks.stream().forEach(this::checkAndExecute);
        } else {
            log.log(Level.WARNING, "provider is not configured ''{0}''", provider.getName());
        }
    }

    private void checkAndExecute(ProviderTask task) {
        log.log(Level.INFO, "executing task ''{0}''", task.getName());
        Duration pauseDuration = Preconditions.checkNotNull(task.getPauseDuration());
        taskService.runIfExpired(task.getName(), pauseDuration, () -> execute(task));
    }

    private void execute(ProviderTask task) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        log.log(Level.INFO, task.getLogPrefix() + "''{0}'' executed", task.getName());
        task.execute();
        long elapsed = stopwatch.elapsed(TimeUnit.SECONDS);
        log.log(Level.INFO, task.getLogPrefix() + "''{0}'' finished in ''{1}'' seconds", new Object[]{task.getName(), elapsed});
    }

}
