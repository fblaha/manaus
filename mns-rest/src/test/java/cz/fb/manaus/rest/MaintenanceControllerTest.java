package cz.fb.manaus.rest;

import cz.fb.manaus.core.maintanance.ConfigUpdate;
import cz.fb.manaus.core.maintanance.PeriodicMaintenanceTask;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Duration;

import static org.junit.Assert.assertThat;
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
        return Duration.ofNanos(777);
    }

    @Override
    public ConfigUpdate execute() {
        ConfigUpdate command = ConfigUpdate.empty(Duration.ofHours(12));
        command.getDeletePrefixes().add("test_delete");
        return command;
    }
}


@ContextConfiguration(classes = MaintenanceController.class)
public class MaintenanceControllerTest extends AbstractControllerTest {

    @Test
    public void testTasks() throws Exception {
        checkResponse("/maintenance", "testTask", "777");
    }

    @Test
    public void testRunTask() throws Exception {
        MvcResult result = mvc.perform(post(
                "/maintenance/{name}", "testTask")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        assertThat(content, CoreMatchers.containsString("test_delete"));
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