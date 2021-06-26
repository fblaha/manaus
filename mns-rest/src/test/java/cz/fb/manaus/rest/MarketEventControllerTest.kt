package cz.fb.manaus.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import cz.fb.manaus.core.model.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime


@ExperimentalTime
@ActiveProfiles("ischia")
class MarketEventControllerTest : AbstractControllerTest() {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `push snapshot`() {
        createLiveMarket()
        val bet = Bet(
                "1",
                market.id, SEL_DRAW,
                Price(3.0, 5.0, Side.BACK),
                Instant.now().minus(2, ChronoUnit.HOURS)
        )
        val crate = MarketEvent(
                prices = runnerPrices,
                bets = listOf(bet),
                account = mbAccount,
                scanTime = 1000,
                tradedVolume = tradedVolume
        )

        val snapshot = objectMapper.writer().writeValueAsString(crate)
        val result = mvc.perform(
                post("/markets/{id}/event", market.id)
                        .content(snapshot)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk)
                .andReturn()
        val collected: CollectedBets = objectMapper.readValue(result.response.contentAsString)
        assertEquals(2, collected.place.size)
        assertEquals(1, collected.update.size)
    }
}