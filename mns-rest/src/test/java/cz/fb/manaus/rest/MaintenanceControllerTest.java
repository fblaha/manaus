package cz.fb.manaus.rest;

import cz.fb.manaus.core.maintanance.PeriodicMaintenanceTask;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

import java.time.Duration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Component
class TestTask implements PeriodicMaintenanceTask {

    @Override
    public String getName() {
        return "testTask";
    }

    @Override
    public Duration getPausePeriod() {
        return Duration.ofMinutes(10);
    }

    @Override
    public void run() {

    }
}


@ContextConfiguration(classes = MaintenanceController.class)
public class MaintenanceControllerTest extends AbstractControllerTest {

    @Test
    public void testTasks() throws Exception {
        checkResponse("/maintenance", "testTask");
    }

    @Test
    public void testRunTask() throws Exception {
        mvc.perform(post(
                "/maintenance/{name}", "testTask")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void testRunTaskNotFound() throws Exception {
        mvc.perform(post(
                "/maintenance/{name}", "missingTask")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }
}