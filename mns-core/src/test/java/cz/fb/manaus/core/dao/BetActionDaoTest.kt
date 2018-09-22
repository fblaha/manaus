package cz.fb.manaus.core.dao

import com.google.common.collect.ImmutableMap.of
import com.google.common.collect.Ordering
import cz.fb.manaus.core.model.BetAction
import cz.fb.manaus.core.model.BetActionType
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.CoreTestFactory
import cz.fb.manaus.core.test.CoreTestFactory.Companion.newMarket
import cz.fb.manaus.core.test.CoreTestFactory.Companion.newTestMarket
import cz.fb.manaus.core.test.ModelFactory
import org.apache.commons.lang3.time.DateUtils
import org.apache.commons.lang3.time.DateUtils.addHours
import org.hamcrest.CoreMatchers.hasItem
import org.hibernate.LazyInitializationException
import org.junit.Assert
import org.junit.Assert.assertThat
import org.junit.Test
import java.util.*
import java.util.Collections.singletonMap
import java.util.Optional.empty
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BetActionDaoTest : AbstractDaoTest() {

    @Test
    fun `get action IDs for given market`() {
        val market = newTestMarket()
        marketDao.saveOrUpdate(market)
        createAndSaveBetAction(market, Date(), singletonMap("k1", "XXX"), AbstractDaoTest.BET_ID)
        val ids = betActionDao.getBetActionIds(market.id, OptionalLong.empty(), empty())
        assertThat(ids, hasItem(AbstractDaoTest.BET_ID))
    }

    @Test
    fun `update bet id`() {
        val market = newTestMarket()
        marketDao.saveOrUpdate(market)
        createAndSaveBetAction(market, Date(), emptyMap(), AbstractDaoTest.BET_ID)
        val newId = AbstractDaoTest.BET_ID + "_1"
        assertEquals(1, betActionDao.updateBetId(AbstractDaoTest.BET_ID, newId))
        assertEquals(0, betActionDao.updateBetId(AbstractDaoTest.BET_ID, newId))
        assertTrue(betActionDao.getBetAction(newId).isPresent)
        assertFalse(betActionDao.getBetAction(AbstractDaoTest.BET_ID).isPresent)
    }

    @Test
    fun `set bet id`() {
        val market = newTestMarket()
        marketDao.saveOrUpdate(market)
        val action = createAndSaveBetAction(market, Date(), emptyMap(), null)
        val actionId = action.id
        assertEquals(1, betActionDao.setBetId(actionId!!, "111"))
        assertEquals("111", betActionDao.get(actionId).get().betId)
    }

    @Test
    fun `get criteria`() {
        val market = newTestMarket()
        marketDao.saveOrUpdate(market)
        createAndSaveBetAction(market, Date(), singletonMap("k1", "XXX"), AbstractDaoTest.BET_ID)
        checkCount(market.id, OptionalLong.empty(), empty(), 1)
        checkCount(market.id, OptionalLong.of(CoreTestFactory.DRAW), empty(), 1)
        checkCount(market.id, OptionalLong.of(CoreTestFactory.DRAW + 1), empty(), 0)
        checkCount(market.id, OptionalLong.of(CoreTestFactory.DRAW), Optional.of(Side.LAY), 1)
        checkCount(market.id, OptionalLong.of(CoreTestFactory.DRAW), Optional.of(Side.BACK), 0)
        checkCount(market.id, OptionalLong.empty(), Optional.of(Side.LAY), 1)
        checkCount(market.id, OptionalLong.empty(), Optional.of(Side.BACK), 0)
    }

    private fun checkCount(marketId: String, selId: OptionalLong, side: Optional<Side>, expectedCount: Int) {
        assertEquals(expectedCount, betActionDao.getBetActions(marketId, selId, side).size)
        assertEquals(expectedCount, betActionDao.getBetActionIds(marketId, selId, side).size)
    }

    @Test
    fun `action properties`() {
        val market = newMarket("33", Date(), CoreTestFactory.MATCH_ODDS)
        marketDao.saveOrUpdate(market)

        createAndSaveBetAction(market, Date(), singletonMap("k1", "newer"), AbstractDaoTest.BET_ID)
        val action = betActionDao.getBetActions("33", OptionalLong.of(CoreTestFactory.DRAW), Optional.of(Side.LAY))[0]
        betActionDao.getBetActions(OptionalInt.empty())
        assertEquals(1, action.properties.size)
        assertEquals("newer", action.properties["k1"])
    }


    @Test
    fun `action properties order`() {
        val market = newMarket("33", Date(), CoreTestFactory.MATCH_ODDS)
        marketDao.saveOrUpdate(market)
        createAndSaveBetAction(market, Date(), singletonMap("k1", "newer"), AbstractDaoTest.BET_ID)
        createAndSaveBetAction(market, addHours(Date(), -1), singletonMap("k1", "older"), AbstractDaoTest.BET_ID + 1)
        var action = betActionDao.getBetActions("33", OptionalLong.of(CoreTestFactory.DRAW), Optional.of(Side.LAY))[0]
        assertEquals(1, action.properties.size)
        assertEquals("older", action.properties["k1"])

        action = betActionDao.getBetActions("33", OptionalLong.of(CoreTestFactory.DRAW), Optional.of(Side.LAY))[1]
        assertEquals(1, action.properties.size)
        assertEquals("newer", action.properties["k1"])
        // delete with properties
        marketDao.delete(market.id)
    }

    @Test
    fun `action with properties delete`() {
        val market = newMarket("33", Date(), CoreTestFactory.MATCH_ODDS)
        marketDao.saveOrUpdate(market)
        createAndSaveBetAction(market, addHours(Date(), -1), singletonMap("k1", "older"), AbstractDaoTest.BET_ID)

        assertTrue(betActionDao.getBetAction(AbstractDaoTest.BET_ID).isPresent)
        marketDao.delete(market.id)
        assertFalse(betActionDao.getBetAction(AbstractDaoTest.BET_ID).isPresent)
    }

    @Test
    fun `no duplicate actions with multiple properties in result set`() {
        val market = newMarket("33", Date(), CoreTestFactory.MATCH_ODDS)
        marketDao.saveOrUpdate(market)
        createAndSaveBetAction(market, Date(), of("k1", "v1", "k2", "v2"), AbstractDaoTest.BET_ID)
        assertEquals(1, betActionDao.getBetActions(OptionalInt.empty()).size)
        assertEquals(1, betActionDao.getBetActions("33", OptionalLong.of(CoreTestFactory.DRAW), Optional.of(Side.LAY)).size)
    }

    @Test
    fun `maximal results limit`() {
        val market = newMarket("33", Date(), CoreTestFactory.MATCH_ODDS)
        marketDao.saveOrUpdate(market)
        createAndSaveBetAction(market, Date(), of("k1", "v1", "k2", "v2"), AbstractDaoTest.BET_ID)
        createAndSaveBetAction(market, Date(), of("k1", "v1", "k2", "v2"), AbstractDaoTest.BET_ID + 1)
        assertEquals(1, betActionDao.getBetActions(OptionalInt.of(1)).size)
        assertEquals(2, betActionDao.getBetActions(OptionalInt.of(2)).size)
    }

    @Test
    fun `get action by bet ID`() {
        createMarketWithSingleAction()
        assertTrue(betActionDao.getBetAction(AbstractDaoTest.BET_ID).isPresent)
        assertFalse(betActionDao.getBetAction(AbstractDaoTest.BET_ID + 1).isPresent)
    }


    @Test
    fun `get action date by bet ID`() {
        createMarketWithSingleAction()
        assertTrue(betActionDao.getBetActionDate(AbstractDaoTest.BET_ID).isPresent)
        assertFalse(betActionDao.getBetActionDate(AbstractDaoTest.BET_ID + 1).isPresent)
    }

    @Test
    fun `market prices field`() {
        createMarketWithSingleAction()
        val betAction = betActionDao.getBetAction(AbstractDaoTest.BET_ID).get()
        assertNotNull(betAction.marketPrices)
    }

    @Test(expected = LazyInitializationException::class)
    fun `lazy fetching market prices - negative`() {
        createMarketWithSingleAction()
        val betAction = betActionDao.getBetAction(AbstractDaoTest.BET_ID).get()
        betAction.marketPrices.getReciprocal(Side.BACK)
    }

    @Test
    fun `lazy fetching market prices - positive`() {
        createMarketWithSingleAction()
        val action = betActionDao.getBetAction(AbstractDaoTest.BET_ID).get()
        betActionDao.fetchMarketPrices(action)
        assertNotNull(action.marketPrices.time)
        assertEquals(0.8333333333333333, action.marketPrices.getReciprocal(Side.BACK).asDouble)
        Assert.assertEquals(1.2, action.marketPrices.getOverround(Side.BACK).asDouble, 0.0001)
    }

    @Test(expected = LazyInitializationException::class)
    fun `lazy fetching market - negative`() {
        createMarketWithSingleAction()
        val action = betActionDao.getBetAction(AbstractDaoTest.BET_ID).get()
        action.marketPrices.market.name
    }

    @Test
    fun `runner count`() {
        val market = newMarket("33", Date(), CoreTestFactory.MATCH_ODDS)
        marketDao.saveOrUpdate(market)
        createAndSaveBetAction(market, addHours(Date(), -1), AbstractDaoTest.PROPS, AbstractDaoTest.BET_ID)
        val stored = betActionDao.getBetAction(AbstractDaoTest.BET_ID).get()
        assertEquals(market.runners.size, stored.market.runners.size)
    }

    @Test
    fun `no impact of save order - asc`() {
        saveActionsAndCheckOrder(compareBy { it.actionDate })
    }

    @Test
    fun `no impact of save order - desc`() {
        saveActionsAndCheckOrder(compareByDescending { it.actionDate })
    }

    private fun saveActionsAndCheckOrder(comparator: Comparator<BetAction>) {
        val market = newMarket("33", Date(), CoreTestFactory.MATCH_ODDS)
        marketDao.saveOrUpdate(market)
        val earlier = ModelFactory.newAction(BetActionType.PLACE, DateUtils.addDays(Date(), -1), Price(2.0, 30.0, Side.LAY), market, CoreTestFactory.DRAW)
        earlier.betId = AbstractDaoTest.BET_ID
        val later = ModelFactory.newAction(BetActionType.PLACE, Date(), Price(3.0, 33.0, Side.LAY), market, CoreTestFactory.DRAW)
        later.betId = AbstractDaoTest.BET_ID + 1
        val actions = listOf(later, earlier)
        Ordering.from(comparator).immutableSortedCopy<BetAction>(actions).forEach { betActionDao.saveOrUpdate(it) }
        val betActionsForMarket = betActionDao.getBetActions(market.id, OptionalLong.empty(), empty())
        assertEquals(2, betActionsForMarket.size)
        assertEquals(2.0, betActionsForMarket[0].price.price)
        assertEquals(3.0, betActionsForMarket[1].price.price)
    }

    @Test
    fun `no duplicates due to multiple runner prices`() {
        val market = newTestMarket()
        val runnerPrices = ModelFactory.newRunnerPrices(232, listOf(Price(2.3, 22.0, Side.BACK)), 5.0, 2.5)
        val marketPrices = ModelFactory.newPrices(1, market, listOf(runnerPrices), Date())
        marketPrices.time = DateUtils.addMonths(Date(), -1)
        marketDao.saveOrUpdate(market)
        marketPricesDao.saveOrUpdate(marketPrices)
        val betAction = CoreTestFactory.newBetAction("1", market)
        betAction.marketPrices = marketPrices
        betActionDao.saveOrUpdate(betAction)

        val actions = betActionDao.getBetActions(market.id, OptionalLong.empty(), empty())
        assertEquals(1, actions.size)
    }

    @Test
    fun `market prices entity is shared by 2 actions`() {
        val market = newTestMarket()
        val runnerPrices = ModelFactory.newRunnerPrices(232, listOf(Price(2.3, 22.0, Side.BACK)), 5.0, 2.5)
        val marketPrices = ModelFactory.newPrices(1, market, listOf(runnerPrices), Date())
        marketPrices.time = DateUtils.addMonths(Date(), -1)
        marketDao.saveOrUpdate(market)
        marketPricesDao.saveOrUpdate(marketPrices)

        val betAction1 = CoreTestFactory.newBetAction("1", market)
        betAction1.marketPrices = marketPrices
        betActionDao.saveOrUpdate(betAction1)

        val betAction2 = CoreTestFactory.newBetAction("2", market)
        betAction2.marketPrices = marketPrices
        betActionDao.saveOrUpdate(betAction2)

        betActionDao.fetchMarketPrices(betAction1)
        betActionDao.fetchMarketPrices(betAction2)

        assertEquals(betAction1.marketPrices.id, betAction2.marketPrices.id)
    }
}