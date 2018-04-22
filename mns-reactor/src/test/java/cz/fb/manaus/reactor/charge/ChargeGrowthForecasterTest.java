package cz.fb.manaus.reactor.charge;

import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.MarketSnapshot;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.provider.ExchangeProvider;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import cz.fb.manaus.core.test.CoreTestFactory;
import cz.fb.manaus.reactor.ReactorTestFactory;
import cz.fb.manaus.reactor.betting.AmountAdviser;
import cz.fb.manaus.reactor.price.Fairness;
import cz.fb.manaus.reactor.price.FairnessPolynomialCalculator;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.Optional;
import java.util.OptionalDouble;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class ChargeGrowthForecasterTest extends AbstractLocalTestCase {

    @Autowired
    private ChargeGrowthForecaster forecaster;
    @Autowired
    private ReactorTestFactory factory;
    @Autowired
    private FairnessPolynomialCalculator calculator;
    @Autowired
    private AmountAdviser adviser;
    @Autowired
    private ExchangeProvider provider;

    @Test
    public void testForecast() throws Exception {
        MarketPrices market = factory.createMarket(0.05, Arrays.asList(0.5, 0.3, 0.2));
        LinkedList<Bet> currentBets = new LinkedList<>();
        MarketSnapshot marketSnapshot = new MarketSnapshot(market, currentBets, Optional.empty());
        Fairness fairness = calculator.getFairness(market);
        OptionalDouble forecast = forecaster.getForecast(CoreTestFactory.DRAW, Side.BACK, marketSnapshot, fairness);
        assertTrue(forecast.getAsDouble() > 1);
        double betAmount = adviser.getAmount();
        currentBets.add(new Bet("1", CoreTestFactory.MARKET_ID, CoreTestFactory.DRAW,
                new Price(3d, betAmount, Side.LAY), new Date(), betAmount));
        forecast = forecaster.getForecast(CoreTestFactory.DRAW, Side.BACK, marketSnapshot, fairness);
        assertFalse(forecast.getAsDouble() > 1);

        forecast = forecaster.getForecast(CoreTestFactory.HOME, Side.BACK, marketSnapshot, fairness);
        assertTrue(forecast.getAsDouble() > 1);

        forecast = forecaster.getForecast(CoreTestFactory.HOME, Side.LAY, marketSnapshot, fairness);
        assertFalse(forecast.getAsDouble() > 1);
    }
}