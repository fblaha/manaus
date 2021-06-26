package cz.fb.manaus.rest

import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.model.market
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.time.ExperimentalTime


@ExperimentalTime
class BetActionControllerTest : AbstractControllerTest() {

    private lateinit var bet: RealizedBet

    @BeforeEach
    fun setUp() {
        bet = createLiveMarket()
    }

    @Test
    fun `action list`() {
        checkResponse("/actions", "betActionType", "time")
        checkResponse("/markets/" + market.id + "/actions", "betActionType", "time")
    }

    @Test
    fun acknowledge() {
        mvc.perform(
            put("/actions/{id}/ack", bet.betAction.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("100")
        )
            .andExpect(status().isOk)
            .andReturn()
    }
}