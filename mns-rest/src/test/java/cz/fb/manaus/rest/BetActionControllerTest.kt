package cz.fb.manaus.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.net.HttpHeaders
import cz.fb.manaus.core.dao.AbstractDaoTest
import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.test.CoreTestFactory
import cz.fb.manaus.core.test.CoreTestFactory.Companion.newTestMarket
import cz.fb.manaus.core.test.ModelFactory
import org.junit.Before
import org.junit.Test
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*
import kotlin.test.assertNotNull


@ContextConfiguration(classes = [BetActionController::class])
class BetActionControllerTest : AbstractControllerTest() {

    private lateinit var bet: SettledBet

    @Before
    fun setUp() {
        bet = createMarketWithSingleSettledBet()
    }

    @Test
    fun `action list`() {
        checkResponse("/actions", "betActionType", "actionDate")
        checkResponse("/markets/" + AbstractDaoTest.MARKET_ID + "/actions", "betActionType", "actionDate")
    }

    @Test
    fun `post action`() {
        val mapper = ObjectMapper()
        val original = createBetAction()
        val priceId = marketPricesDao.getPrices(AbstractDaoTest.MARKET_ID)[0].id
        val serialized = mapper.writer().writeValueAsString(original)
        val result = mvc.perform(post(
                "/markets/{id}/actions?priceId={priceId}", AbstractDaoTest.MARKET_ID, priceId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialized))
                .andExpect(status().isCreated)
                .andReturn()
        assertNotNull(result.response.getHeader(HttpHeaders.LOCATION))
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

    private fun createBetAction(): BetAction {
        val betAction = ModelFactory.newAction(BetActionType.UPDATE, Date(),
                Price(2.0, 3.0, Side.LAY), newTestMarket(), CoreTestFactory.DRAW)
        betAction.properties = Collections.singletonMap("key", "val")
        betAction.betId = "150"
        return betAction
    }
}