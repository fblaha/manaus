package cz.fb.manaus.core.model

import com.fasterxml.jackson.databind.ObjectMapper
import cz.fb.manaus.core.model.factory.BetActionFactory
import org.hamcrest.CoreMatchers.containsString
import org.junit.Assert.assertThat
import org.junit.Test
import java.util.*

class BetActionTest {

    @Test
    fun `json marshall`() {
        val price = Price(5.0, 5.0, Side.BACK)
        val action = BetActionFactory.create(BetActionType.PLACE, Date(), price, null, 100)
        val json = ObjectMapper().writeValueAsString(action)
        assertThat(json, containsString("PLACE"))
    }
}