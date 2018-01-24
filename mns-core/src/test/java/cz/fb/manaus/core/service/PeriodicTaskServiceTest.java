package cz.fb.manaus.core.service;

import cz.fb.manaus.core.test.AbstractDatabaseTestCase;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

public class PeriodicTaskServiceTest extends AbstractDatabaseTestCase {

    public static final String TASK_NAME = "test.task";
    @Autowired
    private PeriodicTaskService service;
    @Autowired
    private PropertiesService propertiesService;

    @Test
    public void testExpiry() throws Exception {
        assertTrue(service.isRefreshRequired(TASK_NAME, Duration.ofMinutes(120),
                Optional.empty()));
        assertFalse(service.isRefreshRequired(TASK_NAME, Duration.ofMinutes(120),
                Optional.of(Instant.now())));
        assertTrue(service.isRefreshRequired(TASK_NAME, Duration.ofMinutes(120),
                Optional.of(Instant.now().minus(3, ChronoUnit.HOURS))));
    }

    @Test
    public void testRunFirst() throws Exception {
        reset(propertiesService);
        when(propertiesService.getInstant(anyString())).thenReturn(Optional.empty());
        checkTaskExecution(1);
    }

    @Test
    public void testFresh() throws Exception {
        reset(propertiesService);
        when(propertiesService.getInstant(anyString()))
                .thenReturn(Optional.of(Instant.now()));
        checkTaskExecution(0);
    }

    @Test
    public void testRunExpired() throws Exception {
        reset(propertiesService);
        when(propertiesService.getInstant(anyString()))
                .thenReturn(Optional.of(Instant.now().minus(3, ChronoUnit.HOURS)));
        checkTaskExecution(1);
    }

    private void checkTaskExecution(int expectedCalls) {
        Runnable mock = Mockito.mock(Runnable.class);
        service.runIfExpired(TASK_NAME, Duration.ofMinutes(10), mock);
        Mockito.verify(mock, Mockito.times(expectedCalls)).run();
    }
}