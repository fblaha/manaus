package cz.fb.manaus.reactor.categorizer;

import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

import static cz.fb.manaus.core.test.CoreTestFactory.newSettledBet;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class CounterBetCategorizerTest extends AbstractLocalTestCase {

    @Autowired
    private CounterBetCategorizer categorizer;

    @Test
    public void testCategoryEq() throws Exception {
        BetCoverage coverage = BetCoverage.from(List.of(newSettledBet(2d, Side.BACK)));
        assertThat(categorizer.getCategories(newSettledBet(2d, Side.LAY), coverage),
                is(Set.of("counter_zero")));
    }

    @Test
    public void testCategoryGt() throws Exception {
        BetCoverage coverage = BetCoverage.from(List.of(newSettledBet(2.5d, Side.BACK)));
        assertThat(categorizer.getCategories(newSettledBet(2d, Side.LAY), coverage),
                is(Set.of("counter_profit")));
    }

    @Test
    public void testCategoryLt() throws Exception {
        BetCoverage coverage = BetCoverage.from(List.of(newSettledBet(1.5d, Side.BACK)));
        assertThat(categorizer.getCategories(newSettledBet(2d, Side.LAY), coverage),
                is(Set.of("counter_loss")));
    }

    @Test
    public void testCategoryNone() throws Exception {
        BetCoverage coverage = BetCoverage.from(List.of(newSettledBet(2d, Side.LAY)));
        assertThat(categorizer.getCategories(newSettledBet(2d, Side.LAY), coverage),
                is(Set.of("counter_none")));
    }

}