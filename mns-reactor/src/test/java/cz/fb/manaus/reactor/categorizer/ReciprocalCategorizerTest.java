package cz.fb.manaus.reactor.categorizer;

import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import cz.fb.manaus.core.test.CoreTestFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ReciprocalCategorizerTest extends AbstractLocalTestCase {

    @Autowired
    private ReciprocalCategorizer categorizer;

    @Test
    public void testCategory() throws Exception {
        assertThat(categorizer.getCategories(CoreTestFactory.newSettledBet(2d, Side.LAY), BetCoverage.EMPTY),
                is(Collections.singleton("reciprocal_0.80-0.85")));
    }

}