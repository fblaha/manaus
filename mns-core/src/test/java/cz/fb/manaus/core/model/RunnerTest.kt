package cz.fb.manaus.core.model

import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.CoreMatchers.containsString
import org.junit.Assert.assertThat
import org.junit.Test

class RunnerTest {

    @Test
    fun `json marshall`() {
        val runner = ModelFactory.newRunner(100, "Sparta", 0.0, 0)
        val json = ObjectMapper().writeValueAsString(runner)
        assertThat(json, containsString("Sparta"))
    }
}