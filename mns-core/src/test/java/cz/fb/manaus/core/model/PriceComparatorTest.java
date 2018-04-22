package cz.fb.manaus.core.model;

import org.junit.Test;

import java.util.List;

import static cz.fb.manaus.core.model.PriceComparator.ORDERING;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PriceComparatorTest {

    @Test
    public void testComparisonLay() throws Exception {
        assertTrue(ORDERING.isOrdered(List.of(new Price(2d, 5d, Side.LAY), new Price(2.1d, 5d, Side.LAY))));
        assertTrue(ORDERING.isOrdered(List.of(new Price(2d, 5d, Side.LAY), new Price(2d, 5d, Side.LAY))));
        assertFalse(ORDERING.isOrdered(List.of(new Price(2.1d, 5d, Side.LAY), new Price(2d, 5d, Side.LAY))));
    }

    @Test
    public void testComparisonBack() throws Exception {
        assertTrue(ORDERING.isOrdered(List.of(new Price(2.1d, 5d, Side.BACK), new Price(2d, 5d, Side.BACK))));
        assertTrue(ORDERING.isOrdered(List.of(new Price(2d, 5d, Side.BACK), new Price(2d, 5d, Side.BACK))));
        assertFalse(ORDERING.isOrdered(List.of(new Price(2d, 5d, Side.BACK), new Price(2.1d, 5d, Side.BACK))));
    }

    @Test
    public void testComparison() throws Exception {
        assertTrue(ORDERING.isOrdered(List.of(new Price(2d, 5d, Side.BACK), new Price(2d, 5d, Side.LAY))));
        assertFalse(ORDERING.isOrdered(List.of(new Price(2d, 5d, Side.LAY), new Price(2d, 5d, Side.BACK))));
    }


}
