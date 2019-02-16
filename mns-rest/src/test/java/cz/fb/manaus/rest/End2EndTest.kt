package cz.fb.manaus.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.common.net.HttpHeaders
import cz.fb.manaus.core.MarketCategories
import cz.fb.manaus.core.model.*
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class End2EndTest : AbstractControllerTest() {

    @Autowired
    private lateinit var objectMapper: ObjectMapper
    private lateinit var collectedBets: CollectedBets

    @Test
    fun `E2E API flow scenario`() {
        `When I post market`()
        `And I post snapshot and I collect bets`()
        `Then bet actions are associated with the market and bet IDs are null`()
        `When I set bet ID for all bet actions`()
        `Then all bet actions should have non empty bet ID`()
        `When I post settled bets for all bet actions`()
        `Then settled bets should be reflected in profit records`()
        `And settled bets should be reflected in fc progress records`()
    }

    private fun `When I post market`() {
        val market = objectMapper.writer().writeValueAsString(market)
        val result = mvc.perform(MockMvcRequestBuilders.post("/markets")
                .content(market)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andReturn()
        assertNotNull(result.response.getHeader(HttpHeaders.LOCATION))
    }

    private fun `And I post snapshot and I collect bets`() {
        val crate = MarketSnapshotCrate(
                prices = runnerPrices,
                bets = emptyList(),
                categoryBlacklist = emptySet(),
                scanTime = 1000,
                tradedVolume = tradedVolume)

        val snapshot = objectMapper.writer().writeValueAsString(crate)
        val result = mvc.perform(MockMvcRequestBuilders.post("/markets/{id}/snapshot", market.id)
                .content(snapshot)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()
        collectedBets = objectMapper.readValue(result.response.contentAsString)
        assertEquals(3, collectedBets.place.size)
        assertEquals(0, collectedBets.update.size)
    }

    private fun `When I set bet ID for all bet actions`() {
        for ((i, bet) in collectedBets.place.withIndex()) {
            mvc.perform(MockMvcRequestBuilders.put(
                    "/actions/{id}/betId", bet.actionId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(i.toString()))
                    .andExpect(MockMvcResultMatchers.status().isOk)
                    .andReturn()
        }
    }


    private fun `When I post settled bets for all bet actions`() {
        for ((i, bet) in collectedBets.place.withIndex()) {
            val settledBet = homeSettledBet.copy(
                    selectionId = bet.selectionId,
                    id = i.toString(),
                    price = bet.requestedPrice,
                    profitAndLoss = 10.0,
                    commission = 0.1
            )
            val serialized = objectMapper.writer().writeValueAsString(settledBet)
            mvc.perform(MockMvcRequestBuilders.post("/bets")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(serialized))
                    .andExpect(MockMvcResultMatchers.status().`is`(202))
                    .andReturn()
        }
    }

    private fun `Then settled bets should be reflected in profit records`() {
        val result = mvc.perform(MockMvcRequestBuilders.get("/profit/1d").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()
        val profitRecords: List<ProfitRecord> = objectMapper.readValue(result.response.contentAsString)
        val allRecord = profitRecords.first()
        assertEquals(MarketCategories.ALL, allRecord.category)
        assertEquals(30.0, allRecord.theoreticalProfit)
        assertEquals(3, allRecord.backCount)
        assertEquals(0, allRecord.layCount)
        assertTrue(allRecord.avgPrice in 3.4..3.5)
    }

    private fun `And settled bets should be reflected in fc progress records`() {
        val result = mvc.perform(MockMvcRequestBuilders.get("/fc-progress/1d").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()
        val profitRecords: List<ProfitRecord> = objectMapper.readValue(result.response.contentAsString)
        assertTrue(profitRecords.isNotEmpty())
    }

    private fun `Then all bet actions should have non empty bet ID`() {
        checkAction(3) { assertNotNull(it.betId) }
    }

    private fun `Then bet actions are associated with the market and bet IDs are null`() {
        checkAction(3) {
            assertNull(it.betId)
            assertEquals(3, it.runnerPrices.size)
            assertEquals(BetActionType.PLACE, it.betActionType)
            assertTrue { it.proposers.isNotEmpty() }
        }
    }

    private fun checkAction(expectedCount: Int, actionCheck: (BetAction) -> Unit) {
        val result = mvc.perform(MockMvcRequestBuilders.get("/markets/" + market.id + "/actions")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()
        val betActions: List<BetAction> = objectMapper.readValue(result.response.contentAsString)
        assertEquals(expectedCount, betActions.size)
        betActions.forEach { actionCheck(it) }
    }
}