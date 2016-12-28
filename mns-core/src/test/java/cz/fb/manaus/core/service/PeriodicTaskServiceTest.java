package cz.fb.manaus.core.service;

import cz.fb.manaus.core.test.AbstractDatabaseTestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.verification.VerificationMode;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class PeriodicTaskServiceTest extends AbstractDatabaseTestCase {

    public static final String TASK_NAME = "test.task";
    @Autowired
    private PeriodicTaskService service;
    @Autowired
    private PropertiesService propertiesService;

    @Before
    public void setUp() throws Exception {
        propertiesService.delete(PeriodicTaskService.PREFIX);
    }

    @Test
    public void testExpiry() throws Exception {
        assertTrue(service.isRefreshRequired(TASK_NAME, Duration.ofMinutes(120)));
        service.markUpdated(TASK_NAME);
        assertFalse(service.isRefreshRequired(TASK_NAME, Duration.ofMinutes(120)));
        service.setTimestamp(TASK_NAME, Instant.now().minus(3, ChronoUnit.HOURS));
        assertTrue(service.isRefreshRequired(TASK_NAME, Duration.ofMinutes(120)));
    }


    @Test
    public void testRunFirst() throws Exception {
        checkTaskExecution(Mockito.times(1));
    }

    @Test
    public void testFresh() throws Exception {
        service.markUpdated(TASK_NAME);
        checkTaskExecution(Mockito.never());
    }

    @Test
    public void testRunExpired() throws Exception {
        service.setTimestamp(TASK_NAME, Instant.now().minus(3, ChronoUnit.HOURS));
        checkTaskExecution(Mockito.times(1));
    }

    private void checkTaskExecution(VerificationMode verificationMode) {
        Runnable mock = Mockito.mock(Runnable.class);
        service.runIfExpired(TASK_NAME, Duration.ofMinutes(10), mock);
        service.runIfExpired(TASK_NAME, Duration.ofMinutes(10), mock);
        Mockito.verify(mock, verificationMode).run();
    }


}