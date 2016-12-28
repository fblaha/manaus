package cz.fb.manaus.core.category.categorizer;

import cz.fb.manaus.core.test.AbstractLocalTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;

public class SelectionRegexpCategorizerTest extends AbstractLocalTestCase {

    @Autowired
    private SelectionRegexpCategorizer categorizer;

    @Test
    public void testMatchOdds() throws Exception {
        assertThat(categorizer.getCategories("The Draw"), hasItem("selectionRegexp_draw"));
    }

}
