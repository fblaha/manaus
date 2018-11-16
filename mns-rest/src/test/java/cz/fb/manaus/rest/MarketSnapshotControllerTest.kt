package cz.fb.manaus.rest

import com.fasterxml.jackson.databind.ObjectMapper
import cz.fb.manaus.core.model.*
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant


@ContextConfiguration(classes = [MarketSnapshotController::class])
class MarketSnapshotControllerTest : AbstractControllerTest() {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `push snapshot`() {
        createLiveMarket()
        val accountMoney = AccountMoney(2000.0, 1000.0)
        val categoryBlacklist = setOf("bad")
        val bet = Bet("1", market.id, SEL_DRAW, Price(3.0, 5.0, Side.BACK), Instant.now())
        val crate = MarketSnapshotCrate(
                prices = runnerPrices,
                bets = listOf(bet),
                categoryBlacklist = categoryBlacklist,
                money = accountMoney,
                scanTime = 1000)

        val snapshot = objectMapper.writer().writeValueAsString(crate)
        mvc.perform(post("/markets/{id}/snapshot", market.id)
                .content(snapshot)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent)
                .andReturn()
    }
}