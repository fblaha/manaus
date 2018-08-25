package cz.fb.manaus.core.test

import cz.fb.manaus.core.dao.BetActionDao
import cz.fb.manaus.core.model.*
import cz.fb.manaus.spring.ManausProfiles
import org.apache.commons.lang3.time.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import java.util.*

@Repository
@Profile(ManausProfiles.DB)
class CoreTestFactory {

    @Autowired
    private lateinit var betActionDao: BetActionDao

    fun savePlaceAction(unmatched: Bet, market: Market): BetAction {
        val betAction = ModelFactory.newAction(BetActionType.PLACE, unmatched.placedDate,
                unmatched.requestedPrice, market, unmatched.selectionId)
        betAction.betId = unmatched.betId
        betActionDao.saveOrUpdate(betAction)
        return betAction
    }

    companion object {

        const val MARKET_ID = "44"
        const val HOME: Long = 1000
        const val DRAW: Long = 1001
        const val AWAY: Long = 1002
        const val MATCH_ODDS = "Match Odds"
        const val HOME_NAME = "Home"
        const val DRAW_NAME = "Draw"
        const val AWAY_NAME = "Away"
        const val AMOUNT = 44.0
        const val EVENT_NAME = "Manaus FC  Rio Negro AM"
        const val COUNTRY_CODE = "BR"


        fun newMarket(id: String, curr: Date, name: String): Market {
            val market = Market()
            market.id = id
            market.name = name
            market.matchedAmount = AMOUNT
            market.isBspMarket = false
            market.isInPlay = false

            market.eventType = EventType("1", "Soccer")
            market.competition = Competition("7", "UA League")
            market.event = newEvent(curr)
            market.runners = runners
            market.type = "MATCH_ODDS"
            return market
        }

        fun newBetAction(betId: String, market: Market): BetAction {
            val betAction = ModelFactory.newAction(BetActionType.PLACE, DateUtils.addHours(Date(), -5), Price(2.0, 2.0, Side.LAY), market, 11)
            betAction.properties = HashMap()
            betAction.betId = betId
            return betAction
        }

        private val runners: Collection<Runner>
            get() {
                val home = ModelFactory.newRunner(HOME, HOME_NAME, 0.0, 1)
                val draw = ModelFactory.newRunner(DRAW, DRAW_NAME, 0.0, 2)
                val away = ModelFactory.newRunner(AWAY, AWAY_NAME, 0.0, 3)
                return listOf(draw, home, away)
            }

        fun newEvent(curr: Date): Event {
            return ModelFactory.newEvent("77", EVENT_NAME, curr, COUNTRY_CODE)
        }

        fun newTestMarket(): Market {
            return newMarket(MARKET_ID, DateUtils.addHours(Date(), 2), MATCH_ODDS)
        }

        fun newBackRP(currPrice: Double, selectionId: Long, lastMatchedPrice: Double?): RunnerPrices {
            return ModelFactory.newRunnerPrices(selectionId, listOf(
                    Price(currPrice, 100.0, Side.BACK),
                    Price(1.4, 100.0, Side.BACK),
                    Price(1.3, 100.0, Side.BACK)), 10.0, lastMatchedPrice)
        }

        fun newMarketPrices(winnerCount: Int, bestBackPrice: Double): MarketPrices {
            val market = newTestMarket()
            val runnerPrices = listOf(
                    newBackRP(bestBackPrice, 1, 2.5),
                    newBackRP(bestBackPrice, 2, 2.5),
                    newBackRP(bestBackPrice, 3, 2.5))
            return ModelFactory.newPrices(winnerCount, market, runnerPrices, Date())
        }

        fun newTestMarketPrices(market: Market): MarketPrices {
            val home = newBackRP(2.5, HOME, 3.0)
            val draw = newBackRP(2.5, DRAW, 3.0)
            val away = newBackRP(2.5, AWAY, 3.0)
            return ModelFactory.newPrices(1, market, listOf(home, draw, away), Date())
        }

        fun newSettledBet(price: Double, side: Side): SettledBet {
            val bet = ModelFactory.newSettled(CoreTestFactory.HOME, "Home", 2.0, Date(), Price(price, 2.0, side))
            val market = newTestMarket()
            val action = newBetAction("1", market)
            action.marketPrices = CoreTestFactory.newTestMarketPrices(market)
            bet.betAction = action
            return bet
        }
    }
}