package cz.fb.manaus.core.model

import com.fasterxml.jackson.databind.ObjectMapper
import cz.fb.manaus.core.model.factory.EventFactory
import org.hamcrest.CoreMatchers.containsString
import org.junit.Assert.assertThat
import org.junit.Test
import java.util.*

class EventTest {

    @Test
    fun `json marshall`() {
        val event = EventFactory.create("100", "Sparta vs Ostrava", Date(), "cz")
        val json = ObjectMapper().writeValueAsString(event)
        assertThat(json, containsString("Ostrava"))
    }
}