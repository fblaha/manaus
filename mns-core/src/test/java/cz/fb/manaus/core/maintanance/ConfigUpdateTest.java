package cz.fb.manaus.core.maintanance;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.time.Duration;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

public class ConfigUpdateTest {

    @Test
    public void testJson() throws Exception {
        ConfigUpdate command = ConfigUpdate.empty(Duration.ofHours(8));
        command.getDeletePrefixes().add("test_delete");
        command.getSetProperties().put("test_key", "test value");
        String json = new ObjectMapper().writeValueAsString(command);
        assertThat(json, containsString("test value"));
        assertThat(json, containsString("test_delete"));
    }
}