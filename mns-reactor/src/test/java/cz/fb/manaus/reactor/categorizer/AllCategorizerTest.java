package cz.fb.manaus.reactor.categorizer;

import cz.fb.manaus.core.MarketCategories;
import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import cz.fb.manaus.core.test.CoreTestFactory;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.junit.Assert.assertThat;

public class AllCategorizerTest extends AbstractLocalTestCase {

    @Autowired
    private AllCategorizer categorizer;

    @Test
    public void testCategory() throws Exception {
        assertThat(categorizer.getCategories(CoreTestFactory.newSettledBet(2d, Side.LAY), BetCoverage.EMPTY),
                CoreMatchers.is(Set.of(MarketCategories.ALL)));
    }

}