package cz.fb.manaus.reactor.price;

import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.PriceComparator;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.model.TradedVolume;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class PriceBulldozerTest extends AbstractLocalTestCase {

    @Autowired
    private PriceBulldozer bulldozer;

    private List<Price> SAMPLE = Arrays.asList(
            new Price(6d, 2d, Side.BACK),
            new Price(3d, 4d, Side.BACK),
            new Price(2.8, 10d, Side.BACK));

    @Test
    public void testBulldozeSimple() throws Exception {
        checkResult(3, Arrays.asList(new Price(3, 2, Side.LAY), new Price(4, 2, Side.LAY)), 1, 3.5, 4);
        List<Price> three = Arrays.asList(new Price(3, 2, Side.LAY),
                new Price(4, 2, Side.LAY), new Price(5, 2, Side.LAY));
        checkResult(3, three, 2, 3.5, 4);
        checkResult(5, three, 1, 4, 6);
    }

    @Test
    public void testBulldozeSingle() throws Exception {
        checkResult(3, singletonList(new Price(3, 2, Side.LAY)), 1, 3, 2);
        checkResult(1, singletonList(new Price(3, 2, Side.LAY)), 1, 3, 2);
    }

    @Test
    public void testBulldozeBoundary() throws Exception {
        List<Price> two = Arrays.asList(new Price(4, 2, Side.BACK), new Price(3, 2, Side.BACK));
        List<Price> three = Arrays.asList(new Price(5, 2, Side.BACK), new Price(4, 2, Side.BACK),
                new Price(3, 2, Side.BACK));

        checkResult(2, two, 2, 4, 2);
        checkResult(4, three, 2, 4.5, 4);
    }

    @Test
    public void testReal() throws Exception {
        checkResult(1, SAMPLE, 3, 6, 2);
        checkResult(2, SAMPLE, 3, 6, 2);
        checkResult(3, SAMPLE, 2, 4, 6);
        checkResult(4, SAMPLE, 2, 4, 6);
        checkResult(5, SAMPLE, 2, 4, 6);
        checkResult(6, SAMPLE, 2, 4, 6);
        checkResult(50, SAMPLE, 1, 3.25, 16);
        checkResult(100, SAMPLE, 1, 3.25, 16);
    }

    @Test(expected = IllegalStateException.class)
    public void testBadOrderBack() throws Exception {
        List<Price> badOrder = PriceComparator.ORDERING.reverse().sortedCopy(SAMPLE);
        bulldozer.bulldoze(10, badOrder);
    }

    @Test(expected = IllegalStateException.class)
    public void testBadOrderLay() throws Exception {
        bulldozer.bulldoze(10, Arrays.asList(new Price(5d, 2, Side.LAY), new Price(4d, 2, Side.LAY)));
    }

    private void checkResult(double threshold, List<Price> prices, int expectedCount,
                             double expectedPrice, double expectedAmount) {
        List<Price> bulldozed = bulldozer.bulldoze(threshold, prices);
        assertThat(bulldozed.size(), is(expectedCount));
        assertEquals(expectedPrice, bulldozed.get(0).getPrice(), 0.0001);
        assertEquals(expectedAmount, bulldozed.get(0).getAmount(), 0.0001);

        assertEquals(prices.stream().mapToDouble(Price::getAmount).sum(),
                bulldozed.stream().mapToDouble(Price::getAmount).sum(), 0.0001);
        assertEquals(TradedVolume.getWeightedMean(prices).getAsDouble(),
                TradedVolume.getWeightedMean(bulldozed).getAsDouble(), 0.0001);
    }

}