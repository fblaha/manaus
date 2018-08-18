package cz.fb.manaus.core.model

import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.CoreMatchers.containsString
import org.junit.Assert.assertThat
import org.junit.Test

class RunnerPricesTest {

    @Test
    fun `json marshall`() {
        val prices = ModelFactory.newRunnerPrices(111, listOf(), 100.0, 5.0)
        val json = ObjectMapper().writeValueAsString(prices)
        assertThat(json, containsString("111"))
    }
}