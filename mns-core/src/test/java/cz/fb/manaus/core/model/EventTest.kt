package cz.fb.manaus.core.model

import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.CoreMatchers.containsString
import org.junit.Assert.assertThat
import org.junit.Test
import java.util.*

class EventTest {

    @Test
    fun `json marshall`() {
        val event = ModelFactory.newEvent("100", "Sparta vs Ostrava", Date(), "cz")
        val json = ObjectMapper().writeValueAsString(event)
        assertThat(json, containsString("Ostrava"))
    }
}