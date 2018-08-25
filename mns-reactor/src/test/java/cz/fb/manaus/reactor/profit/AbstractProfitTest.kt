package cz.fb.manaus.reactor.profit

import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.core.test.CoreTestFactory
import org.apache.commons.lang3.time.DateUtils
import org.apache.commons.lang3.time.DateUtils.addDays
import org.junit.Before
import java.util.*

abstract class AbstractProfitTest : AbstractLocalTestCase() {
    protected lateinit var marketDate: Date
    private lateinit var market: Market

    @Before
    fun setUp() {
        marketDate = addDays(DateUtils.truncate(Date(), Calendar.MONTH), -10)
        market = CoreTestFactory.newMarket("1", marketDate, CoreTestFactory.MATCH_ODDS)
        market.eventType = EventType("1", "soccer")
    }

    protected fun setBetAction(vararg bets: SettledBet) {
        bets.forEachIndexed { i, b ->
            b.betAction = CoreTestFactory.newBetAction(Integer.toString(i + 1), market)
        }
    }

    // TODO not use java Optional in kotlin code
    protected fun generateBets(requestedSide: Optional<Side>): List<SettledBet> {
        val result = mutableListOf<SettledBet>()
        var price = 1.5
        while (price < 4) {
            addSideBets(result, price, Side.LAY, requestedSide)
            addSideBets(result, price + 0.1, Side.BACK, requestedSide)
            price += 0.02
        }
        setBetAction(*result.toTypedArray())
        return result
    }

    private fun addSideBets(result: MutableList<SettledBet>, price: Double, side: Side, requestedSide: Optional<Side>) {
        if (requestedSide.orElse(side) === side) {
            result.add(ModelFactory.newSettled(CoreTestFactory.DRAW, "The Draw", 5.0, marketDate,
                    Price(price, 4.0, side)))
            result.add(ModelFactory.newSettled(CoreTestFactory.HOME, "Home", 5.0, marketDate,
                    Price(price, 4.0, side)))
        }
    }
}
