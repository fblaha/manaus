package cz.fb.manaus.reactor.charge

import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.test.AbstractTestCase5
import cz.fb.manaus.reactor.PricesTestFactory
import cz.fb.manaus.reactor.betting.AmountAdviser
import cz.fb.manaus.reactor.price.FairnessPolynomialCalculator
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class ChargeGrowthForecasterTest : AbstractTestCase5() {

    @Autowired
    private lateinit var forecaster: ChargeGrowthForecaster

    @Autowired
    private lateinit var adviser: AmountAdviser

    @Test
    fun forecast() {
        val marketPrices = PricesTestFactory.newMarketPrices(0.05, listOf(0.5, 0.3, 0.2))
        val currentBets = mutableListOf<TrackedBet>()
        val snapshot = MarketSnapshot(marketPrices, market, currentBets)
        val fairness = FairnessPolynomialCalculator.getFairness(marketPrices)
        val commission = bfProvider.commission
        val homeBack = SideSelection(Side.BACK, SEL_HOME)
        var forecast = forecaster.getForecast(homeBack, snapshot, fairness, commission)
        assertTrue(forecast!! > 1)
        val betAmount = adviser.amount
        currentBets.add(
                Bet(
                        "1", "1", SEL_DRAW,
                        Price(3.0, betAmount, Side.LAY),
                        Instant.now(),
                        betAmount
                ).asTracked
        )
        val drawBack = SideSelection(Side.BACK, SEL_DRAW)
        forecast = forecaster.getForecast(drawBack, snapshot, fairness, commission)
        assertFalse(forecast!! > 1)

        forecast = forecaster.getForecast(homeBack, snapshot, fairness, commission)
        assertTrue(forecast!! > 1)

        val homeLay = SideSelection(Side.LAY, SEL_HOME)
        forecast = forecaster.getForecast(homeLay, snapshot, fairness, commission)
        assertFalse(forecast!! > 1)
    }
}