package cz.fb.manaus.reactor.betting.listener

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.core.test.CoreTestFactory
import cz.fb.manaus.core.test.ModelFactory
import cz.fb.manaus.reactor.betting.action.BetUtils
import org.apache.commons.lang3.time.DateUtils
import org.hamcrest.Matchers.closeTo
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

class BetUtilsTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var betUtils: BetUtils
    private lateinit var bet: SettledBet


    @Before
    fun setUp() {
        val price = Price(5.0, 3.0, Side.BACK)
        bet = ModelFactory.newSettled(CoreTestFactory.DRAW, CoreTestFactory.DRAW_NAME,
                5.0, Date(), price)
        bet.betAction = ModelFactory.newAction(BetActionType.PLACE, Date(), price, CoreTestFactory.newTestMarket(), 1000)
    }

    @Test
    fun `current actions`() {
        val currDate = Date()
        val priceBack = Price(2.0, 2.0, Side.BACK)
        val priceLay = Price(1.8, 2.0, Side.LAY)
        val selectionId = 1
        val back1 = ModelFactory.newAction(BetActionType.PLACE, DateUtils.addHours(currDate, -10), priceBack,
                mock(), selectionId.toLong())
        val back2 = ModelFactory.newAction(BetActionType.PLACE, DateUtils.addHours(currDate, -9), priceBack,
                mock(), selectionId.toLong())
        val back3 = ModelFactory.newAction(BetActionType.PLACE, DateUtils.addHours(currDate, -8), priceBack,
                mock(), selectionId.toLong())
        val lay1 = ModelFactory.newAction(BetActionType.PLACE, DateUtils.addHours(currDate, -6), priceLay,
                mock(), selectionId.toLong())
        val lay2 = ModelFactory.newAction(BetActionType.UPDATE, DateUtils.addHours(currDate, -5), priceLay,
                mock(), selectionId.toLong())
        val lay3 = ModelFactory.newAction(BetActionType.PLACE, DateUtils.addHours(currDate, -4), priceLay,
                mock(), selectionId.toLong())
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
        val action = mock<BetAction>()
        whenever(action.betId).thenReturn("1", "2")
        val bet = mock<Bet>()
        whenever(bet.betId).thenReturn("1")
        assertEquals(0, betUtils.getUnknownBets(listOf(bet), setOf("1")).size)
        assertEquals(1, betUtils.getUnknownBets(listOf(bet), setOf("2")).size)
    }

    @Test
    fun `ceil amount settled bet`() {
        val ceilCopy = betUtils.limitBetAmount(2.0, bet)
        assertNotSame(bet, ceilCopy)
        assertEquals(bet.selectionName, ceilCopy.selectionName)
        assertEquals(bet.selectionId, ceilCopy.selectionId)
        assertThat(ceilCopy.profitAndLoss, closeTo(bet.profitAndLoss * 2.0 / 3, 0.001))
    }

    @Test
    fun `ceil amount bet action`() {
        val ceilCopy = betUtils.limitBetAmount(2.0, bet)
        val action = bet.betAction
        val actionCopy = ceilCopy.betAction
        assertNotSame(action, actionCopy)
        assertEquals(action.actionDate, actionCopy.actionDate)
        assertEquals(action.selectionId, actionCopy.selectionId)
        assertEquals(2.0, actionCopy.price.amount)
    }

    @Test
    fun `bellow ceiling - returns the same instances`() {
        val ceilCopy = betUtils.limitBetAmount(100.0, bet)
        val action = bet.betAction
        val actionCopy = ceilCopy.betAction

        assertSame(bet, ceilCopy)
        assertSame(bet.price, ceilCopy.price)
        assertSame(action, actionCopy)
        assertSame(action.price, actionCopy.price)
    }
}
