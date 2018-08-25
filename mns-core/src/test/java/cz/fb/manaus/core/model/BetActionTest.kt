package cz.fb.manaus.core.model

import com.fasterxml.jackson.databind.ObjectMapper
import cz.fb.manaus.core.test.CoreTestFactory.Companion.newTestMarket
import org.hamcrest.CoreMatchers.containsString
import org.junit.Assert.assertThat
import org.junit.Test
import java.util.*

class BetActionTest {

    @Test
    fun `json marshall`() {
        val price = Price(5.0, 5.0, Side.BACK)
        val action = ModelFactory.newAction(BetActionType.PLACE, Date(), price, newTestMarket(), 100)
        val json = ObjectMapper().writeValueAsString(action)
        assertThat(json, containsString("PLACE"))
    }
}