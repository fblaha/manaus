package cz.fb.manaus.rest

import com.fasterxml.jackson.databind.ObjectMapper
import cz.fb.manaus.core.model.ProfitRecord
import cz.fb.manaus.core.model.betAction
import cz.fb.manaus.core.model.drawSettledBet
import cz.fb.manaus.core.model.homeSettledBet
import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertTrue

class JsonMarshallerTest : AbstractLocalTestCase() {
    @Autowired
    private lateinit var mapper: ObjectMapper

    @Test
    fun `bet action list`() {
        val action = betAction.copy(proposers = setOf("bestPrice"))
        val json = mapper.writer().writeValueAsString(listOf(action))
        assertTrue { "bestPrice" in json }
    }

    @Test
    fun `settled bet list`() {
        val json = mapper.writer().writeValueAsString(listOf(homeSettledBet, drawSettledBet))
        assertTrue { "The Draw" in json }
    }

    @Test
    fun `profit record list`() {
        val profitRecord = ProfitRecord("test_name", 5.0, 2.0, 0.2, 2, 1)
        val json = mapper.writer().writeValueAsString(listOf(profitRecord))
        assertTrue { "test_name" in json }
    }
}
