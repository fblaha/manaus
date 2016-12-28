package cz.fb.manaus.core.model;

import cz.fb.manaus.core.test.CoreTestFactory;
import org.apache.commons.math3.util.Precision;
import org.junit.Test;

import static com.google.common.collect.ImmutableList.of;
import static cz.fb.manaus.core.model.MarketPrices.getOverround;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class MarketPricesTest {


    @Test
    public void testReciprocal() throws Exception {
        assertThat(CoreTestFactory.newMarketPrices(1, 2.4d).getReciprocal(Side.BACK).getAsDouble(), is(0.8d));
        assertThat(CoreTestFactory.newMarketPrices(1, 3d).getReciprocal(Side.BACK).getAsDouble(), is(1d));
    }

    @Test
    public void testReciprocalTwoWinners() throws Exception {
        assertThat(CoreTestFactory.newMarketPrices(2, 1.5d).getReciprocal(Side.BACK).getAsDouble(), is(1d));
    }

    @Test
    public void testLastMatchedReciprocal() throws Exception {
        assertThat(CoreTestFactory.newMarketPrices(null).getLastMatchedReciprocal().getAsDouble(), is(1d));
    }

    @Test
    public void testReciprocalTwoWinnersCompare() throws Exception {
        assertTrue(CoreTestFactory.newMarketPrices(2, 1.45d).getReciprocal(Side.BACK).getAsDouble() <
                CoreTestFactory.newMarketPrices(2, 1.46d).getReciprocal(Side.BACK).getAsDouble());
    }

    @Test
    public void testOverround() throws Exception {
        double overround = getOverround(of(2.5d, 3.25d, 3d));
        assertThat(Precision.round(overround, 3), is(1.041));
    }

}
