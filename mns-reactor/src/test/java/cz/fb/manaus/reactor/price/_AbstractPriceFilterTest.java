package cz.fb.manaus.reactor.price;

import com.google.common.collect.Range;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import cz.fb.manaus.reactor.ReactorTestFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class _AbstractPriceFilterTest extends AbstractLocalTestCase {


    public static final Price BACK1 = new Price(1.96d, 5d, Side.BACK);
    public static final Price LAY1 = new Price(2.04d, 5d, Side.LAY);
    public static final Price BACK2 = new Price(1.92d, 5d, Side.BACK);
    public static final Price LAY2 = new Price(2.1d, 5d, Side.LAY);
    public static final Price BACK3 = new Price(1.90d, 5d, Side.BACK);
    public static final Price LAY3 = new Price(2.15d, 5d, Side.LAY);
    public static final List<Price> SAMPLE_PRICES = List.of(LAY1, LAY2,
            LAY3, new Price(2.2d, 5d, Side.LAY),
            new Price(1.8d, 5d, Side.BACK), new Price(1.82d, 5d, Side.BACK),
            BACK2, new Price(1.88d, 5d, Side.BACK),
            BACK3, BACK1);


    @Autowired
    private TestFilter filter;
    @Autowired
    private ReactorTestFactory testFactory;

    @Test
    public void testSignificantPricesSize() {
        assertThat(filter.getSignificantPrices(1, SAMPLE_PRICES).size(), is(2));
        assertThat(filter.getSignificantPrices(2, SAMPLE_PRICES).size(), is(4));
        assertThat(filter.getSignificantPrices(3, SAMPLE_PRICES).size(), is(6));
        assertThat(filter.getSignificantPrices(4, SAMPLE_PRICES).size(), is(8));
        assertThat(filter.getSignificantPrices(5, SAMPLE_PRICES).size(), is(9));
        assertThat(filter.getSignificantPrices(6, SAMPLE_PRICES).size(), is(10));
    }

    @Test
    public void testSignificantPrices() {
        assertThat(filter.getSignificantPrices(1, SAMPLE_PRICES), is(List.of(BACK1, LAY1)));
        assertThat(filter.getSignificantPrices(2, SAMPLE_PRICES), is(List.of(BACK1, BACK2, LAY1, LAY2)));
        assertThat(filter.getSignificantPrices(3, SAMPLE_PRICES), is(List.of(BACK1, BACK2, BACK3, LAY1, LAY2, LAY3)));
    }


    @Test
    public void testBestPrices() {
        var market = testFactory.createMarket(0.15, List.of(0.5, 0.3, 0.2));
        for (var runnerPrices : market.getRunnerPrices()) {
            var bestBack = runnerPrices.getHomogeneous(Side.BACK).getBestPrice().get();
            var bestLay = runnerPrices.getHomogeneous(Side.LAY).getBestPrice().get();
            var prices = runnerPrices.getPrices().stream().collect(toList());
            var filteredPrices = this.filter.filter(prices);
            var bySide = filteredPrices.stream()
                    .collect(toMap(Price::getSide, identity()));
            assertThat(bestBack, is(bySide.get(Side.BACK)));
            assertThat(bestLay, is(bySide.get(Side.LAY)));
        }
    }

    @Component
    private static class TestFilter extends AbstractPriceFilter {

        public TestFilter() {
            super(1, -1, Range.all());
        }

    }

}