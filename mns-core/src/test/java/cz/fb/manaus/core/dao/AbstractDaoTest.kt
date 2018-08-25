package cz.fb.manaus.core.dao

import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.test.AbstractDatabaseTestCase
import cz.fb.manaus.core.test.CoreTestFactory
import cz.fb.manaus.core.test.CoreTestFactory.newMarket
import org.apache.commons.lang3.time.DateUtils
import org.apache.commons.lang3.time.DateUtils.addHours
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Assert.assertThat
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import javax.persistence.EntityManagerFactory

abstract class AbstractDaoTest : AbstractDatabaseTestCase() {

    @Autowired
    protected lateinit var betActionDao: BetActionDao
    @Autowired
    protected lateinit var marketDao: MarketDao
    @Autowired
    protected lateinit var marketPricesDao: MarketPricesDao
    @Autowired
    protected lateinit var settledBetDao: SettledBetDao
    @Autowired
    protected lateinit var factory: EntityManagerFactory

    @After
    fun cleanUp() {
        marketDao.deleteMarkets(Date.from(Instant.now().plus(5000, ChronoUnit.DAYS)))
    }

    protected fun createAndSaveBetAction(market: Market, date: Date, values: Map<String, String>, betId: String?): BetAction {
        val betAction = ModelFactory.newAction(BetActionType.PLACE, date, Price(2.0, 3.0, Side.LAY), market, CoreTestFactory.DRAW)
        val marketPrices = CoreTestFactory.newMarketPrices(market)
        marketPricesDao.saveOrUpdate(marketPrices)
        betAction.marketPrices = marketPrices
        betAction.properties = values
        betAction.betId = betId
        betActionDao.saveOrUpdate(betAction)
        return betAction
    }

    protected fun createMarketWithSingleAction() {
        val date = Date.from(Instant.now().plus(5, ChronoUnit.HOURS))
        val market = newMarket(MARKET_ID, date, CoreTestFactory.MATCH_ODDS)
        marketDao.saveOrUpdate(market)
        createAndSaveBetAction(market, addHours(date, -1), PROPS, BET_ID)
    }

    protected fun createMarketWithSingleSettledBet(): SettledBet {
        val now = Date.from(Instant.now())
        val market = newMarket(MARKET_ID, now, CoreTestFactory.MATCH_ODDS)
        marketDao.saveOrUpdate(market)
        val actionDate = Date.from(Instant.now().minus(1, ChronoUnit.HOURS))
        val action = createAndSaveBetAction(market, actionDate, PROPS, BET_ID)
        val bet = ModelFactory.newSettled(action.selectionId, "x", 5.0, now, action.price)
        bet.betAction = action
        settledBetDao.saveOrUpdate(bet)
        return bet
    }

    protected fun createBet() {
        val current = Date()
        val market = newMarket()
        val prices = ModelFactory.newPrices(1, market, createRPs(2.1, 2.2), Date())
        prices.time = DateUtils.addHours(current, -1)
        marketDao.saveOrUpdate(market)
        marketPricesDao.saveOrUpdate(prices)
        betActionDao.saveOrUpdate(createAction(market, prices, "1"))
        val prices2 = ModelFactory.newPrices(1, market, createRPs(2.3, 2.5), Date())
        prices2.time = current
        marketPricesDao.saveOrUpdate(prices2)
        betActionDao.saveOrUpdate(createAction(market, prices2, "2"))
        assertThat(marketPricesDao.getPrices(CoreTestFactory.MARKET_ID).size, `is`(2))
    }

    private fun createAction(market: Market, prices: MarketPrices, betId: String): BetAction {
        val place = ModelFactory.newAction(BetActionType.PLACE, Date(), Price(2.2, 2.1, Side.LAY),
                market, CoreTestFactory.HOME)
        place.betId = betId
        place.marketPrices = prices
        return place
    }

    private fun createRPs(price: Double, lastMatchedPrice: Double): List<RunnerPrices> {
        return listOf(
                CoreTestFactory.newBackRP(price, CoreTestFactory.HOME, lastMatchedPrice),
                CoreTestFactory.newBackRP(price, CoreTestFactory.DRAW, lastMatchedPrice),
                CoreTestFactory.newBackRP(price, CoreTestFactory.AWAY, lastMatchedPrice))
    }

    companion object {

        const val BET_ID = "99999"
        const val SPARTA = "Sparta Praha - Banik Ostrava"
        val PROPS = mapOf("reciprocal" to "0.9", "lastMatched" to "2")
        const val MARKET_ID = "33"
    }

}
