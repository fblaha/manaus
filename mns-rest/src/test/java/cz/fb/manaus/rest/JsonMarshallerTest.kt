package cz.fb.manaus.rest

import com.fasterxml.jackson.databind.ObjectMapper
import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.core.test.CoreTestFactory.Companion.newBetAction
import cz.fb.manaus.core.test.CoreTestFactory.Companion.newTestMarket
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*
import kotlin.test.assertTrue

class JsonMarshallerTest : AbstractLocalTestCase() {
    @Autowired
    private lateinit var mapper: ObjectMapper

    @Test
    fun `bet action list`() {
        val action = ModelFactory.newAction(BetActionType.PLACE, Date(), Price(2.0, 5.0, Side.LAY),
                newTestMarket(), 10)
        val props = mapOf("property1" to "value1", "reciprocal" to "0.92")
        action.properties = props
        val json = mapper.writer().writeValueAsString(listOf(action))
        assertTrue { "value1" in json }
    }

    @Test
    fun `settled bet list`() {
        val bet = ModelFactory.newSettled(555, "The Draw", 5.23, Date(), Price(2.02, 2.35, Side.LAY))
        bet.betAction = newBetAction("1", newTestMarket())
        val json = mapper.writer().writeValueAsString(listOf(bet))
        assertTrue { "The Draw" in json }
    }

    @Test
    fun `profit record list`() {
        val profitRecord = ProfitRecord("test_name", 5.0, 2, 1, 2.0, 0.2)
        val json = mapper.writer().writeValueAsString(listOf(profitRecord))
        assertTrue { "test_name" in json }
    }
}
