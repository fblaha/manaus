package cz.fb.manaus.core.maintanance

import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.CoreMatchers.containsString
import org.junit.Assert.assertThat
import org.junit.Test
import java.time.Duration

class ConfigUpdateTest {

    @Test
    fun `json marshalling`() {
        val configUpdate = ConfigUpdate.empty(Duration.ofHours(8))
        configUpdate.deletePrefixes.add("test_delete")
        configUpdate.setProperties["test_key"] = "test value"
        val json = ObjectMapper().writeValueAsString(configUpdate)
        assertThat(json, containsString("480m"))
        assertThat(json, containsString("test value"))
        assertThat(json, containsString("test_delete"))
    }
}