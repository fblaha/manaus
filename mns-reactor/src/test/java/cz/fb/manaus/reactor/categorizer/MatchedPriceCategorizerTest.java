package cz.fb.manaus.reactor.categorizer;

import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MatchedPriceCategorizerTest extends AbstractLocalTestCase {

    @Autowired
    private MatchedPriceCategorizer categorizer;

    @Test
    public void testCategoryBetter() throws Exception {
        assertThat(categorizer.getCategory(3.5, 3, Side.BACK), is("matchedPrice_better"));
        assertThat(categorizer.getCategory(2.5, 3, Side.LAY), is("matchedPrice_better"));
    }

    @Test
    public void testCategoryEqual() throws Exception {
        assertThat(categorizer.getCategory(3, 3, Side.BACK), is("matchedPrice_equal"));
        assertThat(categorizer.getCategory(3, 3, Side.LAY), is("matchedPrice_equal"));
    }

    @Test
    public void testCategoryWorse() throws Exception {
        assertThat(categorizer.getCategory(2.5, 3, Side.BACK), is("matchedPrice_worse"));
        assertThat(categorizer.getCategory(3.5, 3, Side.LAY), is("matchedPrice_worse"));
    }

}