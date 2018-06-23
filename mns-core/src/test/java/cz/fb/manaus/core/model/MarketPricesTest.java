package cz.fb.manaus.core.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.fb.manaus.core.test.CoreTestFactory;
import org.apache.commons.math3.util.Precision;
import org.junit.Test;

import java.util.Collection;
import java.util.Date;

import static com.google.common.collect.ImmutableList.of;
import static cz.fb.manaus.core.model.MarketPrices.getOverround;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class MarketPricesTest {


    public static MarketPrices create(int winnerCount, Market market, Collection<RunnerPrices> runnerPrices, Date time) {
        var mp = new MarketPrices();
        mp.setWinnerCount(winnerCount);
        mp.setMarket(market);
        mp.setRunnerPrices(runnerPrices);
        mp.setTime(time);
        return mp;
    }

    @Test
    public void testSerialization() throws Exception {
        var prices = CoreTestFactory.newMarketPrices(1, 2.4d);
        var mapper = new ObjectMapper();
        var serialized = mapper.writer().writeValueAsString(prices);
        MarketPrices restored = mapper.readerFor(MarketPrices.class).readValue(serialized);
        var doubleSerialized = mapper.writer().writeValueAsString(restored);
        assertEquals(serialized, doubleSerialized);
    }

    @Test
    public void testReciprocal() {
        assertThat(CoreTestFactory.newMarketPrices(1, 2.4d).getReciprocal(Side.BACK).getAsDouble(), is(0.8d));
        assertThat(CoreTestFactory.newMarketPrices(1, 3d).getReciprocal(Side.BACK).getAsDouble(), is(1d));
    }

    @Test
    public void testReciprocalTwoWinners() {
        assertThat(CoreTestFactory.newMarketPrices(2, 1.5d).getReciprocal(Side.BACK).getAsDouble(), is(1d));
    }

    @Test
    public void testLastMatchedReciprocal() {
        assertThat(CoreTestFactory.newMarketPrices(null).getLastMatchedReciprocal().getAsDouble(), is(1d));
    }

    @Test
    public void testReciprocalTwoWinnersCompare() {
        assertTrue(CoreTestFactory.newMarketPrices(2, 1.45d).getReciprocal(Side.BACK).getAsDouble() <
                CoreTestFactory.newMarketPrices(2, 1.46d).getReciprocal(Side.BACK).getAsDouble());
    }

    @Test
    public void testOverround() {
        var overround = getOverround(of(2.5d, 3.25d, 3d));
        assertThat(Precision.round(overround, 3), is(1.041));
    }

}
