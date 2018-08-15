package cz.fb.manaus.core.dao

import com.google.common.collect.ImmutableMap.of
import com.google.common.collect.Ordering
import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.test.CoreTestFactory
import cz.fb.manaus.core.test.CoreTestFactory.newMarket
import org.apache.commons.lang3.time.DateUtils
import org.apache.commons.lang3.time.DateUtils.addHours
import org.hamcrest.CoreMatchers.*
import org.hibernate.LazyInitializationException
import org.junit.Assert.*
import org.junit.Test
import java.util.*
import java.util.Collections.singletonMap
import java.util.Comparator.comparing
import java.util.Optional.empty
import kotlin.test.assertNotNull

class BetActionDaoTest : AbstractDaoTest() {

    @Test
    fun testBetIds() {
        val market = newMarket()
        marketDao.saveOrUpdate(market)
        createAndSaveBetAction(market, Date(), singletonMap("k1", "XXX"), AbstractDaoTest.BET_ID)
        val ids = betActionDao.getBetActionIds(market.id, OptionalLong.empty(), empty())
        assertThat(ids, hasItem(AbstractDaoTest.BET_ID))
    }

    @Test
    fun testUpdateBetId() {
        val market = newMarket()
        marketDao.saveOrUpdate(market)
        createAndSaveBetAction(market, Date(), emptyMap(), AbstractDaoTest.BET_ID)
        val newId = AbstractDaoTest.BET_ID + "_1"
        assertThat(betActionDao.updateBetId(AbstractDaoTest.BET_ID, newId), `is`(1))
        assertThat(betActionDao.updateBetId(AbstractDaoTest.BET_ID, newId), `is`(0))
        assertTrue(betActionDao.getBetAction(newId).isPresent)
        assertFalse(betActionDao.getBetAction(AbstractDaoTest.BET_ID).isPresent)
    }

    @Test
    fun testSetBetId() {
        val market = newMarket()
        marketDao.saveOrUpdate(market)
        val action = createAndSaveBetAction(market, Date(), emptyMap(), null)
        val actionId = action.id
        assertThat(betActionDao.setBetId(actionId!!, "111"), `is`(1))
        assertThat(betActionDao.get(actionId).get().betId, `is`("111"))
    }

    @Test
    fun testBetAction() {
        val market = newMarket()
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

    private fun checkCount(marketId: String, selId: OptionalLong, side: Optional<Side>, expectedCount: Long) {
        assertThat(betActionDao.getBetActions(marketId, selId, side).size, `is`(expectedCount.toInt()))
        assertThat(betActionDao.getBetActionIds(marketId, selId, side).size, `is`(expectedCount.toInt()))
    }

    @Test
    fun testBetActionProperties() {
        val market = newMarket("33", Date(), CoreTestFactory.MATCH_ODDS)
        marketDao.saveOrUpdate(market)

        createAndSaveBetAction(market, Date(), singletonMap("k1", "newer"), AbstractDaoTest.BET_ID)
        val action = betActionDao.getBetActions("33", OptionalLong.of(CoreTestFactory.DRAW), Optional.of(Side.LAY))[0]
        betActionDao.getBetActions(OptionalInt.empty())
        assertThat(action.properties.size, `is`(1))
        assertThat<String>(action.properties["k1"], `is`("newer"))
    }


    @Test
    fun testBetActionPropertiesOrder() {
        val market = newMarket("33", Date(), CoreTestFactory.MATCH_ODDS)
        marketDao.saveOrUpdate(market)
        createAndSaveBetAction(market, Date(), singletonMap("k1", "newer"), AbstractDaoTest.BET_ID)
        createAndSaveBetAction(market, addHours(Date(), -1), singletonMap("k1", "older"), AbstractDaoTest.BET_ID + 1)
        var action = betActionDao.getBetActions("33", OptionalLong.of(CoreTestFactory.DRAW), Optional.of(Side.LAY))[0]
        assertThat(action.properties.size, `is`(1))
        assertThat<String>(action.properties["k1"], `is`("older"))

        action = betActionDao.getBetActions("33", OptionalLong.of(CoreTestFactory.DRAW), Optional.of(Side.LAY))[1]
        assertThat(action.properties.size, `is`(1))
        assertThat<String>(action.properties["k1"], `is`("newer"))
        // delete with properties
        marketDao.delete(market.id)
    }

    @Test
    fun testBetActionPropertiesDelete() {
        val market = newMarket("33", Date(), CoreTestFactory.MATCH_ODDS)
        marketDao.saveOrUpdate(market)
        createAndSaveBetAction(market, addHours(Date(), -1), singletonMap("k1", "older"), AbstractDaoTest.BET_ID)

        assertTrue(betActionDao.getBetAction(AbstractDaoTest.BET_ID).isPresent)
        marketDao.delete(market.id)
        assertFalse(betActionDao.getBetAction(AbstractDaoTest.BET_ID).isPresent)
    }

    @Test
    fun testBetActionPropertiesDuplicity() {
        val market = newMarket("33", Date(), CoreTestFactory.MATCH_ODDS)
        marketDao.saveOrUpdate(market)
        createAndSaveBetAction(market, Date(), of("k1", "v1", "k2", "v2"), AbstractDaoTest.BET_ID)
        assertThat(betActionDao.getBetActions(OptionalInt.empty()).size, `is`(1))
        assertThat(betActionDao.getBetActions("33", OptionalLong.of(CoreTestFactory.DRAW), Optional.of(Side.LAY)).size, `is`(1))
    }

    @Test
    fun testMaxResults() {
        val market = newMarket("33", Date(), CoreTestFactory.MATCH_ODDS)
        marketDao.saveOrUpdate(market)
        createAndSaveBetAction(market, Date(), of("k1", "v1", "k2", "v2"), AbstractDaoTest.BET_ID)
        createAndSaveBetAction(market, Date(), of("k1", "v1", "k2", "v2"), AbstractDaoTest.BET_ID + 1)
        assertThat(betActionDao.getBetActions(OptionalInt.of(1)).size, `is`(1))
        assertThat(betActionDao.getBetActions(OptionalInt.of(2)).size, `is`(2))
    }

    @Test
    fun testBetActionByBetId() {
        createMarketWithSingleAction()
        assertTrue(betActionDao.getBetAction(AbstractDaoTest.BET_ID).isPresent)
        assertFalse(betActionDao.getBetAction(AbstractDaoTest.BET_ID + 1).isPresent)
    }


    @Test
    fun testBetActionDateByBetId() {
        createMarketWithSingleAction()
        assertTrue(betActionDao.getBetActionDate(AbstractDaoTest.BET_ID).isPresent)
        assertFalse(betActionDao.getBetActionDate(AbstractDaoTest.BET_ID + 1).isPresent)
    }

    @Test
    fun testMarketPrices() {
        createMarketWithSingleAction()
        val betAction = betActionDao.getBetAction(AbstractDaoTest.BET_ID).get()
        assertNotNull(betAction.marketPrices)
    }

    @Test(expected = LazyInitializationException::class)
    fun testMarketPricesLazy() {
        createMarketWithSingleAction()
        val betAction = betActionDao.getBetAction(AbstractDaoTest.BET_ID).get()
        betAction.marketPrices.getReciprocal(Side.BACK)
    }

    @Test
    fun testMarketPricesLazyFetch() {
        createMarketWithSingleAction()
        val action = betActionDao.getBetAction(AbstractDaoTest.BET_ID).get()
        betActionDao.fetchMarketPrices(action)
        assertThat(action.marketPrices.time, notNullValue())
        assertThat(action.marketPrices.getReciprocal(Side.BACK).asDouble, `is`(0.8333333333333333))
        assertEquals(1.2, action.marketPrices.getOverround(Side.BACK).asDouble, 0.0001)
    }

    @Test(expected = LazyInitializationException::class)
    fun testMarketLazy() {
        createMarketWithSingleAction()
        val action = betActionDao.getBetAction(AbstractDaoTest.BET_ID).get()
        action.marketPrices.market.name
    }

    @Test
    fun testRunnerCount() {
        val market = newMarket("33", Date(), CoreTestFactory.MATCH_ODDS)
        marketDao.saveOrUpdate(market)
        createAndSaveBetAction(market, addHours(Date(), -1), AbstractDaoTest.PROPS, AbstractDaoTest.BET_ID)
        val stored = betActionDao.getBetAction(AbstractDaoTest.BET_ID).get()
        assertThat(stored.market.runners.size, `is`(market.runners.size))
    }

    @Test
    fun testBetActionSortAsc() {
        saveActionsAndCheckOrder(comparing<BetAction, Date>({ it.getActionDate() }))
    }

    @Test
    fun testBetActionSortDesc() {
        saveActionsAndCheckOrder(comparing<BetAction, Date>({ it.getActionDate() }).reversed())
    }

    private fun saveActionsAndCheckOrder(comparator: Comparator<BetAction>) {
        val market = newMarket("33", Date(), CoreTestFactory.MATCH_ODDS)
        marketDao.saveOrUpdate(market)
        val earlier = BetActionTest.create(BetActionType.PLACE, DateUtils.addDays(Date(), -1), Price(2.0, 30.0, Side.LAY), market, CoreTestFactory.DRAW)
        earlier.betId = AbstractDaoTest.BET_ID
        val later = BetActionTest.create(BetActionType.PLACE, Date(), Price(3.0, 33.0, Side.LAY), market, CoreTestFactory.DRAW)
        later.betId = AbstractDaoTest.BET_ID + 1
        val actions = listOf(later, earlier)
        Ordering.from(comparator).immutableSortedCopy<BetAction>(actions).forEach { betActionDao.saveOrUpdate(it) }
        val betActionsForMarket = betActionDao.getBetActions(market.id, OptionalLong.empty(), empty())
        assertThat(betActionsForMarket.size, `is`(2))
        assertThat(2.0, `is`(betActionsForMarket[0].price.price))
        assertThat(3.0, `is`(betActionsForMarket[1].price.price))
    }

    @Test
    fun testBetActionWithRunnerPrices() {
        val market = newMarket()
        val runnerPrices = RunnerPricesTest.create(232, listOf(Price(2.3, 22.0, Side.BACK)), 5.0, 2.5)
        val marketPrices = MarketPricesTest.create(1, market, listOf(runnerPrices), Date())
        marketPrices.time = DateUtils.addMonths(Date(), -1)
        marketDao.saveOrUpdate(market)
        marketPricesDao.saveOrUpdate(marketPrices)
        val betAction = CoreTestFactory.newBetAction("1", market)
        betAction.marketPrices = marketPrices
        betActionDao.saveOrUpdate(betAction)

        val actions = betActionDao.getBetActions(market.id, OptionalLong.empty(), empty())
        assertThat(actions.size, `is`(1))
    }

    @Test
    fun testSharedPrices() {
        val market = newMarket()
        val runnerPrices = RunnerPricesTest.create(232, listOf(Price(2.3, 22.0, Side.BACK)), 5.0, 2.5)
        val marketPrices = MarketPricesTest.create(1, market, listOf(runnerPrices), Date())
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

        assertThat(betAction1.marketPrices.id, `is`(betAction2.marketPrices.id))
    }

}