package cz.fb.manaus.reactor.charge

import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.core.test.CoreTestFactory
import cz.fb.manaus.reactor.ReactorTestFactory
import cz.fb.manaus.reactor.betting.AmountAdviser
import cz.fb.manaus.reactor.price.FairnessPolynomialCalculator
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class ChargeGrowthForecasterTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var forecaster: ChargeGrowthForecaster
    @Autowired
    private lateinit var factory: ReactorTestFactory
    @Autowired
    private lateinit var calculator: FairnessPolynomialCalculator
    @Autowired
    private lateinit var adviser: AmountAdviser

    @Test
    fun forecast() {
        val market = factory.createMarket(0.05, listOf(0.5, 0.3, 0.2))
        val currentBets = LinkedList<Bet>()
        val marketSnapshot = MarketSnapshot.from(market, currentBets, Optional.empty<Map<Long, TradedVolume>>())
        val fairness = calculator.getFairness(market)
        var forecast = forecaster.getForecast(CoreTestFactory.DRAW, Side.BACK, marketSnapshot, fairness)
        assertTrue(forecast!! > 1)
        val betAmount = adviser.amount
        currentBets.add(Bet("1", CoreTestFactory.MARKET_ID, CoreTestFactory.DRAW,
                Price(3.0, betAmount, Side.LAY), Date(), betAmount))
        forecast = forecaster.getForecast(CoreTestFactory.DRAW, Side.BACK, marketSnapshot, fairness)
        assertFalse(forecast!! > 1)

        forecast = forecaster.getForecast(CoreTestFactory.HOME, Side.BACK, marketSnapshot, fairness)
        assertTrue(forecast!! > 1)

        forecast = forecaster.getForecast(CoreTestFactory.HOME, Side.LAY, marketSnapshot, fairness)
        assertFalse(forecast!! > 1)
    }
}