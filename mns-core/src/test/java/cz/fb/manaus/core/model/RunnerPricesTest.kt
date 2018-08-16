package cz.fb.manaus.core.model

import com.fasterxml.jackson.databind.ObjectMapper
import cz.fb.manaus.core.model.factory.RunnerPricesFactory
import org.hamcrest.CoreMatchers.containsString
import org.junit.Assert.assertThat
import org.junit.Test

class RunnerPricesTest {

    @Test
    fun `json marshall`() {
        val prices = RunnerPricesFactory.create(111, listOf(), 100.0, 5.0)
        val json = ObjectMapper().writeValueAsString(prices)
        assertThat(json, containsString("111"))
    }
}