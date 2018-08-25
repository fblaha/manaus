package cz.fb.manaus.rest

import com.fasterxml.jackson.databind.ObjectMapper
import cz.fb.manaus.core.dao.AbstractDaoTest
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.CoreTestFactory
import cz.fb.manaus.core.test.ModelFactory
import org.junit.Before
import org.junit.Test
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*


@ContextConfiguration(classes = [SettledBetController::class])
class SettledBetControllerTest : AbstractControllerTest() {

    @Before
    fun setUp() {
        createMarketWithSingleSettledBet()
    }

    @Test
    fun `bet list`() {
        checkResponse("/bets", "settled")
        checkResponse("/bets/2d", "settled")
        checkResponse("/markets/" + AbstractDaoTest.MARKET_ID + "/bets", "settled")
    }

    @Test
    fun story() {
        checkResponse("/stories/" + AbstractDaoTest.BET_ID, "previousActions")
    }

    @Test
    fun `post settled bet`() {
        val mapper = ObjectMapper()
        val original = ModelFactory.newSettled(CoreTestFactory.DRAW, CoreTestFactory.DRAW_NAME,
                5.0, Date(), Price(5.0, 3.0, Side.BACK))
        val serialized = mapper.writer().writeValueAsString(original)
        checkPost(serialized, AbstractDaoTest.BET_ID, 202)
        checkPost(serialized, AbstractDaoTest.BET_ID + "55", 204)
    }

    private fun checkPost(serialized: String, betId: String, status: Int) {
        mvc.perform(post("/bets?betId={betId}", betId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialized))
                .andExpect(status().`is`(status))
                .andReturn()
    }
}