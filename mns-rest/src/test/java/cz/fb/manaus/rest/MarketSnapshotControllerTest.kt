package cz.fb.manaus.rest

import com.fasterxml.jackson.databind.ObjectMapper
import cz.fb.manaus.core.dao.AbstractDaoTest
import cz.fb.manaus.core.model.AccountMoney
import cz.fb.manaus.core.model.Bet
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.CoreTestFactory
import cz.fb.manaus.core.test.CoreTestFactory.newMarketPrices
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
        createMarketWithSingleAction()
        val marketPrices = newMarketPrices(3, 2.8)
        val crate = MarketSnapshotCrate()
        crate.prices = marketPrices
        val accountMoney = AccountMoney()
        accountMoney.available = 1000.0
        accountMoney.total = 2000.0
        crate.money = accountMoney
        crate.scanTime = 1000
        crate.categoryBlacklist = setOf("bad")
        val bet = Bet("1", marketPrices.market.id, CoreTestFactory.DRAW,
                Price(3.0, 5.0, Side.BACK), Date(), 0.0)
        crate.bets = listOf(bet)
        val snapshot = ObjectMapper().writer().writeValueAsString(crate)
        mvc.perform(post("/markets/{id}/snapshot", AbstractDaoTest.MARKET_ID)
                .content(snapshot)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent)
                .andReturn()
    }
}