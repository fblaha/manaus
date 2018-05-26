package cz.fb.manaus.reactor.betting.listener;

import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import cz.fb.manaus.reactor.ReactorTestFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ProbabilityComparatorTest extends AbstractLocalTestCase {

    @Autowired
    private ReactorTestFactory testFactory;

    @Test
    public void testCompare() {
        var first = testFactory.newRP(1, 1.4d, 1.6d);
        var second = testFactory.newRP(2, 2.8d, 3.3d);
        assertThat(getFirstSelection(List.of(first, first)), is(1L));
        assertThat(getFirstSelection(List.of(first, second)), is(1L));
        assertThat(getFirstSelection(List.of(second, first)), is(1L));
        assertThat(getFirstSelection(List.of(second, second)), is(2L));
    }

    private long getFirstSelection(List<RunnerPrices> lists) {
        return ProbabilityComparator.COMPARATORS.get(Side.BACK).immutableSortedCopy(lists).get(0).getSelectionId();
    }
}