package cz.fb.manaus.core.model;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TradedVolumeTest {

    @Test
    public void testMean() throws Exception {
        assertThat(new TradedVolume(List.of(new Price(2d, 5d, null))).getWeightedMean().getAsDouble(), is(2d));
        assertThat(new TradedVolume(List.of(
                new Price(2d, 5d, null),
                new Price(2d, 10d, null))).getWeightedMean().getAsDouble(), is(2d));
        assertThat(new TradedVolume(List.of(
                new Price(3d, 5d, null),
                new Price(6d, 10d, null))).getWeightedMean().getAsDouble(), is(5d));
        assertThat(new TradedVolume(List.of(
                new Price(3d, 5d, null),
                new Price(9d, 10d, null))).getWeightedMean().getAsDouble(), is(7d));
    }
}