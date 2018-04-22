package cz.fb.manaus.reactor.categorizer;

import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import cz.fb.manaus.core.test.CoreTestFactory;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class FairnessCategorizerTest extends AbstractLocalTestCase {

    @Autowired
    private FairnessCategorizer categorizer;

    @Test
    public void testCategoryLow() throws Exception {
        assertThat(categorizer.getCategory(0.5d), is("fairness_0.50-0.60"));
        assertThat(categorizer.getCategory(0.15d), is("fairness_0.10-0.20"));
        assertThat(categorizer.getCategory(1.5d), is("fairness_1.00+"));
    }

    @Test
    public void testCategoryHigh() throws Exception {
        assertThat(categorizer.getCategory(0.8d), is("fairness_0.80-0.85"));
        assertThat(categorizer.getCategory(0.87d), is("fairness_0.85-0.90"));
    }

    @Test(expected = NullPointerException.class)
    public void testCategoryNegative() throws Exception {
        categorizer.getCategory(-1.5d);
    }

    @Test
    public void testCategory() throws Exception {
        assertThat(categorizer.getCategories(CoreTestFactory.newSettledBet(2d, Side.LAY), BetCoverage.EMPTY),
                CoreMatchers.is(Set.of("fairness_0.75-0.80")));
    }

}