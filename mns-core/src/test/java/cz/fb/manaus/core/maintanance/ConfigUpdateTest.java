package cz.fb.manaus.core.maintanance;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.time.Duration;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

public class ConfigUpdateTest {

    @Test
    public void testJson() throws Exception {
        ConfigUpdate configUpdate = ConfigUpdate.empty(Duration.ofHours(8));
        configUpdate.getDeletePrefixes().add("test_delete");
        configUpdate.getSetProperties().put("test_key", "test value");
        String json = new ObjectMapper().writeValueAsString(configUpdate);
        assertThat(json, containsString("480m"));
        assertThat(json, containsString("test value"));
        assertThat(json, containsString("test_delete"));
    }
}