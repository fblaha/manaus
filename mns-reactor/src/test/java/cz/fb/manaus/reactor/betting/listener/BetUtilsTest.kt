package cz.fb.manaus.reactor.betting.listener

import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.reactor.betting.action.BetUtils
import org.hamcrest.Matchers.closeTo
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals
import kotlin.test.assertNotSame

class BetUtilsTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var betUtils: BetUtils
    private lateinit var bet: SettledBet


    @Before
    fun setUp() {
        val price = Price(5.0, 3.0, Side.BACK)
        bet = homeSettledBet.copy(price = price)
    }

    @Test
    fun `current actions`() {
        val currDate = Instant.now()
        val priceBack = Price(2.0, 2.0, Side.BACK)
        val priceLay = Price(1.8, 2.0, Side.LAY)
        val actionTemplates = betAction.copy(selectionID = 1L)
        val back1 = actionTemplates.copy(
                betActionType = BetActionType.PLACE,
                time = currDate.minus(10, ChronoUnit.HOURS),
                price = priceBack)
        val back2 = actionTemplates.copy(
                betActionType = BetActionType.PLACE,
                time = currDate.minus(9, ChronoUnit.HOURS),
                price = priceBack)
        val back3 = actionTemplates.copy(
                betActionType = BetActionType.PLACE,
                time = currDate.minus(8, ChronoUnit.HOURS),
                price = priceBack)
        val lay1 = actionTemplates.copy(
                betActionType = BetActionType.PLACE,
                time = currDate.minus(6, ChronoUnit.HOURS),
                price = priceLay)
        val lay2 = actionTemplates.copy(
                betActionType = BetActionType.PLACE,
                time = currDate.minus(5, ChronoUnit.HOURS),
                price = priceLay)
        val lay3 = actionTemplates.copy(
                betActionType = BetActionType.PLACE,
                time = currDate.minus(4, ChronoUnit.HOURS),
                price = priceLay)
        var filtered = betUtils.getCurrentActions(listOf(back1, back2, back3))
        assertEquals(1, filtered.size)
        assertEquals(back3, filtered[filtered.size - 1])

        filtered = betUtils.getCurrentActions(listOf(lay1, lay2))
        assertEquals(2, filtered.size)
        assertEquals(lay1, filtered[0])
        assertEquals(lay2, filtered[filtered.size - 1])

        filtered = betUtils.getCurrentActions(listOf(lay1, lay2, lay3))
        assertEquals(1, filtered.size)
        assertEquals(lay3, filtered[filtered.size - 1])
    }

    @Test
    fun `unknown bets`() {
        val bet = Bet("1", "1", 1, Price(3.0, 3.0, Side.BACK))
        assertEquals(0, betUtils.getUnknownBets(listOf(bet), setOf("1")).size)
        assertEquals(1, betUtils.getUnknownBets(listOf(bet), setOf("2")).size)
    }

    @Test
    fun `ceil amount settled bet`() {
        val bet = realizedBet
        val ceilCopy = betUtils.limitBetAmount(2.0, bet)
        assertNotSame(bet, ceilCopy)
        assertEquals(bet.settledBet.selectionName, ceilCopy.settledBet.selectionName)
        assertEquals(bet.settledBet.selectionId, ceilCopy.settledBet.selectionId)
        assertThat(ceilCopy.settledBet.profitAndLoss,
                closeTo(bet.settledBet.profitAndLoss * 2.0 / 3, 0.001))
    }

    @Test
    fun `ceil amount bet action`() {
        val bet = realizedBet
        val ceilCopy = betUtils.limitBetAmount(2.0, bet)
        val action = bet.betAction
        val actionCopy = ceilCopy.betAction
        assertNotSame(action, actionCopy)
        assertEquals(betAction.time, actionCopy.time)
        assertEquals(betAction.selectionID, actionCopy.selectionID)
        assertEquals(2.0, actionCopy.price.amount)
    }

    @Test
    fun `bellow ceiling - returns the same instances`() {
        val bet = realizedBet
        val ceilCopy = betUtils.limitBetAmount(100.0, bet)
        assertEquals(bet, ceilCopy)
    }
}
