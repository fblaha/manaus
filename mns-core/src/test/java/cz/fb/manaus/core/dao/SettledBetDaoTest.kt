package cz.fb.manaus.core.dao

import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.SettledBetTest
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.CoreTestFactory
import cz.fb.manaus.core.test.CoreTestFactory.DRAW_NAME
import cz.fb.manaus.core.test.CoreTestFactory.newMarket
import org.apache.commons.lang3.time.DateUtils.addDays
import org.apache.commons.lang3.time.DateUtils.addHours
import org.junit.Test
import java.util.*
import java.util.Optional.empty
import java.util.Optional.of
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SettledBetDaoTest : AbstractDaoTest() {

    @Test
    fun `get criteria`() {
        val curr = Date()
        val market = newMarket(MARKET_ID, curr, CoreTestFactory.MATCH_ODDS)
        marketDao.saveOrUpdate(market)
        val fooAction = createAndSaveBetAction(market, curr, AbstractDaoTest.PROPS, "foo")
        val settledBet = SettledBetTest.create(555, DRAW_NAME, 5.0,
                curr, Price(3.0, 5.0, Side.LAY))
        settledBet.betAction = fooAction
        settledBetDao.saveOrUpdate(settledBet)
        assertEquals(0, settledBetDao.getSettledBets(of(addHours(curr, 1)), of(Date()), empty(), OptionalInt.empty()).size)
        assertEquals(1, settledBetDao.getSettledBets(of(addHours(curr, -1)), of(Date()), empty(), OptionalInt.empty()).size)

        var bets = settledBetDao.getSettledBets(of(addHours(curr, -1)), of(Date()), empty(), OptionalInt.empty())
        assertEquals(1, bets.size)
        assertEquals(MARKET_ID, bets[0].betAction.market.id)
        assertEquals(DRAW_NAME, bets[0].selectionName)
        assertEquals(1, settledBetDao.getSettledBets(empty(), empty(), empty(), OptionalInt.empty()).size)

        assertEquals(0, settledBetDao.getSettledBets("34", OptionalLong.empty(), empty()).size)
        bets = settledBetDao.getSettledBets(MARKET_ID, OptionalLong.empty(), empty())
        assertEquals(1, bets.size)
        assertEquals(MARKET_ID, bets[0].betAction.market.id)
        assertEquals(DRAW_NAME, bets[0].selectionName)

        assertEquals(1, settledBetDao.getSettledBets(MARKET_ID, OptionalLong.empty(), empty()).size)
        assertEquals(1, settledBetDao.getSettledBets(MARKET_ID, OptionalLong.of(555L), empty()).size)
        assertEquals(1, settledBetDao.getSettledBets(MARKET_ID, OptionalLong.of(555L), of(Side.LAY)).size)
        assertEquals(1, settledBetDao.getSettledBets(MARKET_ID, OptionalLong.empty(), of(Side.LAY)).size)

        assertEquals(0, settledBetDao.getSettledBets(MARKET_ID, OptionalLong.of(556L), empty()).size)
        assertEquals(0, settledBetDao.getSettledBets(MARKET_ID, OptionalLong.of(555L), of(Side.BACK)).size)
    }

    @Test
    fun `get by id`() {
        storeMarketAndBet()
        assertTrue(settledBetDao.getSettledBet(AbstractDaoTest.BET_ID).isPresent)
        assertFalse(settledBetDao.getSettledBet(AbstractDaoTest.BET_ID + 1).isPresent)
    }

    @Test
    fun `saved runner count`() {
        storeMarketAndBet()
        val stored = settledBetDao.getSettledBet(AbstractDaoTest.BET_ID)
        assertEquals(3, stored.get().betAction.market.runners.size)
    }

    @Test
    fun `saved runner count in batch get`() {
        storeMarketAndBet()
        val settledBets = settledBetDao.getSettledBets(of(addDays(Date(), -5)), of(Date()),
                empty(), OptionalInt.empty())
        val stored = settledBets[0]
        assertEquals(3, stored.betAction.market.runners.size)
    }

    @Test
    fun `maximal result limit`() {
        val market = newMarket(MARKET_ID, Date(), CoreTestFactory.MATCH_ODDS)
        marketDao.saveOrUpdate(market)
        val fooAction = createAndSaveBetAction(market, Date(), AbstractDaoTest.PROPS, "foo")
        val barAction = createAndSaveBetAction(market, Date(), AbstractDaoTest.PROPS, "bar")
        val fooBet = SettledBetTest.create(555, DRAW_NAME, 5.0,
                Date(), Price(3.0, 5.0, Side.LAY))
        fooBet.betAction = fooAction
        val barBet = SettledBetTest.create(556, DRAW_NAME, 5.0,
                Date(), Price(3.0, 5.0, Side.LAY))
        barBet.betAction = barAction
        settledBetDao.saveOrUpdate(fooBet)
        settledBetDao.saveOrUpdate(barBet)
        assertEquals(1, settledBetDao.getSettledBets(of(addDays(Date(), -5)), of(Date()), empty(), OptionalInt.of(1)).size)
        assertEquals(2, settledBetDao.getSettledBets(of(addDays(Date(), -5)), of(Date()), empty(), OptionalInt.of(2)).size)
    }


    private fun storeMarketAndBet() {
        val market = newMarket(MARKET_ID, Date(), CoreTestFactory.MATCH_ODDS)
        marketDao.saveOrUpdate(market)
        val fooAction = createAndSaveBetAction(market, Date(), AbstractDaoTest.PROPS, AbstractDaoTest.BET_ID)
        val settledBet = SettledBetTest.create(555, DRAW_NAME, 5.0,
                Date(), Price(3.0, 5.0, Side.LAY))
        settledBet.betAction = fooAction
        settledBetDao.saveOrUpdate(settledBet)
    }
}