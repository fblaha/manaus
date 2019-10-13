package cz.fb.manaus.reactor.charge

import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.reactor.ReactorTestFactory
import cz.fb.manaus.reactor.betting.AmountAdviser
import cz.fb.manaus.reactor.price.FairnessPolynomialCalculator
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant
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
        val marketPrices = factory.newMarketPrices(0.05, listOf(0.5, 0.3, 0.2))
        val currentBets = mutableListOf<Bet>()
        val marketSnapshot = MarketSnapshot.from(marketPrices, market, currentBets)
        val fairness = calculator.getFairness(marketPrices)
        val commission = provider.commission
        var forecast = forecaster.getForecast(SEL_HOME, Side.BACK, marketSnapshot, fairness, commission)
        assertTrue(forecast!! > 1)
        val betAmount = adviser.amount
        currentBets.add(Bet("1", "1", SEL_DRAW,
                Price(3.0, betAmount, Side.LAY), Instant.now(), betAmount))
        forecast = forecaster.getForecast(SEL_DRAW, Side.BACK, marketSnapshot, fairness, commission)
        assertFalse(forecast!! > 1)

        forecast = forecaster.getForecast(SEL_HOME, Side.BACK, marketSnapshot, fairness, commission)
        assertTrue(forecast!! > 1)

        forecast = forecaster.getForecast(SEL_HOME, Side.LAY, marketSnapshot, fairness, commission)
        assertFalse(forecast!! > 1)
    }
}