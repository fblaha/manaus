package cz.fb.manaus.core.model;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MarketSnapshotTest {

    @Test
    public void testMarketCoverage() {
        var selectionId = 1L;
        var side = Side.LAY;
        var predecessor = new Bet(null, null, selectionId, new Price(2d, 2d, side), DateUtils.addHours(new Date(), -2), 1d);
        var successor = new Bet(null, null, selectionId, new Price(2d, 2d, side), new Date(), 1d);
        var coverage = MarketSnapshot.getMarketCoverage(List.of(successor, predecessor));
        assertThat(coverage.size(), is(1));
        assertThat(coverage.get(side, selectionId), is(successor));
    }

}