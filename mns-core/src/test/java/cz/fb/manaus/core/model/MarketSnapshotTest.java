package cz.fb.manaus.core.model;

import com.google.common.collect.Table;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class MarketSnapshotTest {

    @Test
    public void testMarketCoverage() throws Exception {
        long selectionId = 1;
        Side side = Side.LAY;
        Bet predecessor = new Bet(null, null, selectionId, new Price(2d, 2d, side), DateUtils.addHours(new Date(), -2), 1d);
        Bet successor = new Bet(null, null, selectionId, new Price(2d, 2d, side), new Date(), 1d);
        Table<Side, Long, Bet> coverage = MarketSnapshot.getMarketCoverage(Arrays.asList(successor, predecessor));
        assertThat(coverage.size(), is(1));
        assertThat(coverage.get(side, selectionId), is(successor));
        assertThat(successor.getPredecessor(), is(predecessor));
        assertThat(predecessor.getPredecessor(), nullValue());
    }

}