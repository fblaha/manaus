package cz.fb.manaus.ischia.strategy;

import cz.fb.manaus.core.model.Event;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import cz.fb.manaus.reactor.betting.BetContext;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.OptionalDouble;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@ActiveProfiles("ischia")
public class MinimizeChargeStrategyTest extends AbstractLocalTestCase {

    @Autowired
    private MinimizeChargeStrategy strategy;

    @Test
    public void testStrategy() throws Exception {
        BetContext context = Mockito.mock(BetContext.class);
        when(context.getSide()).thenReturn(Side.BACK);
        MarketPrices marketPrices = Mockito.mock(MarketPrices.class);
        Market market = Mockito.mock(Market.class);
        Event event = Mockito.mock(Event.class);
        Date openDate = Date.from(Instant.now().plus(30, ChronoUnit.MINUTES));
        when(event.getOpenDate()).thenReturn(openDate);
        when(market.getEvent()).thenReturn(event);
        when(marketPrices.getMarket()).thenReturn(market);
        when(context.getMarketPrices()).thenReturn(marketPrices);


        when(context.getChargeGrowthForecast()).thenReturn(
                OptionalDouble.empty(),
                OptionalDouble.of(1d / 0d),
                OptionalDouble.of(1.5),
                OptionalDouble.of(0.1));
        assertEquals(strategy.getUpperBoundary(context.getSide()), strategy.getReductionRate(context), 0.000001);
        assertEquals(strategy.getUpperBoundary(context.getSide()), strategy.getReductionRate(context), 0.000001);
        assertEquals(strategy.getUpperBoundary(context.getSide()), strategy.getReductionRate(context), 0.000001);
        assertEquals(strategy.fairnessReductionLow, strategy.getReductionRate(context), 0.000001);
    }


}