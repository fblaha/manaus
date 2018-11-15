package cz.fb.manaus.rest

import com.fasterxml.jackson.databind.ObjectMapper
import cz.fb.manaus.core.model.*
import org.junit.Test
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*


@ContextConfiguration(classes = [MarketSnapshotController::class])
class MarketSnapshotControllerTest : AbstractControllerTest() {

    @Test
    fun `push snapshot`() {
        createLiveMarket()
        val accountMoney = AccountMoney(2000.0, 1000.0)
        val categoryBlacklist = setOf("bad")
        val bet = Bet("1", market.id, SEL_DRAW,
                Price(3.0, 5.0, Side.BACK), Date())
        val crate = MarketSnapshotCrate(runnerPrices, listOf(bet), categoryBlacklist, accountMoney, 1000)

        val snapshot = ObjectMapper().writer().writeValueAsString(crate)
        mvc.perform(post("/markets/{id}/snapshot", market.id)
                .content(snapshot)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent)
                .andReturn()
    }
}