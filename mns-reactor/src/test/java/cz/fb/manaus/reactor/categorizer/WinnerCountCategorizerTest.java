package cz.fb.manaus.reactor.categorizer;

import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import cz.fb.manaus.core.test.CoreTestFactory;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

import static org.junit.Assert.assertThat;

public class WinnerCountCategorizerTest extends AbstractLocalTestCase {

    @Autowired
    private WinnerCountCategorizer categorizer;

    @Test
    public void testCategory() throws Exception {
        assertThat(categorizer.getCategories(CoreTestFactory.newSettledBet(2d, Side.LAY), BetCoverage.EMPTY),
                CoreMatchers.is(Collections.singleton("winnerCount_1")));
    }
}