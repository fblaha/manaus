package cz.fb.manaus.betfair.rest;

import org.junit.Test;

import java.util.List;

import static cz.fb.manaus.betfair.rest.MarketCountAware.split;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MarketCountAwareTest {

    private MarketCountAware create(int count) {
        MarketCountAware mock = mock(MarketCountAware.class);
        when(mock.getMarketCount()).thenReturn(count);
        return mock;
    }

    @Test
    public void testSplitSimple() throws Exception {
        assertThat(split(singletonList(create(50))).size(), is(1));
        assertThat(split(singletonList(create(50))).get(0).size(), is(1));

        assertThat(split(singletonList(create(150))).size(), is(1));
        assertThat(split(singletonList(create(150))).get(0).size(), is(1));
    }

    @Test
    public void testSplit() throws Exception {
        List<List<MarketCountAware>> split = split(asList(create(50), create(50), create(50), create(51)));
        assertThat(split.size(), is(3));
        assertThat(split.get(0).size(), is(2));
        assertThat(split.get(1).size(), is(1));
        assertThat(split.get(2).size(), is(1));
        assertThat(split.get(1).get(0).getMarketCount(), is(50));
        assertThat(split.get(2).get(0).getMarketCount(), is(51));
    }
}