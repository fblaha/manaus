package cz.fb.manaus.rest

import com.fasterxml.jackson.databind.ObjectMapper
import cz.fb.manaus.core.model.homeSettledBet
import org.junit.Before
import org.junit.Test
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@ContextConfiguration(classes = [SettledBetController::class])
class SettledBetControllerTest : AbstractControllerTest() {

    @Before
    fun setUp() {
        createLiveMarket()
    }

    @Test
    fun `bet list`() {
        checkResponse("/bets", "settled")
        checkResponse("/bets/2d", "settled")
        checkResponse("/markets/" + AbstractDaoTest.MARKET_ID + "/bets", "settled")
    }

    @Test
    fun `post settled bet`() {
        val mapper = ObjectMapper()
        val original = homeSettledBet
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