package cz.fb.manaus.rest

import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.model.market
import org.junit.Before
import org.junit.Test
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@ContextConfiguration(classes = [BetActionController::class])
class BetActionControllerTest : AbstractControllerTest() {

    private lateinit var bet: RealizedBet

    @Before
    fun setUp() {
        bet = createLiveMarket()
    }

    @Test
    fun `action list`() {
        checkResponse("/actions", "betActionType", "actionDate")
        checkResponse("/markets/" + market.id + "/actions", "betActionType", "actionDate")
    }

    @Test
    fun `set bet ID`() {
        val actionId = bet.betAction.id
        mvc.perform(put(
                "/actions/{id}/betId", actionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("100"))
                .andExpect(status().isOk)
                .andReturn()
    }
}