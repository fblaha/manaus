package cz.fb.manaus.reactor.categorizer;

import cz.fb.manaus.core.test.AbstractLocalTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PriceCategorizerTest extends AbstractLocalTestCase {

    @Autowired
    private PriceCategorizer categorizer;

    @Test
    public void testGetCategories() throws Exception {
        assertThat(categorizer.getCategory(1.5d), is("priceRange_1.5-2.0"));
        assertThat(categorizer.getCategory(1.8d), is("priceRange_1.5-2.0"));
        assertThat(categorizer.getCategory(1.01d), is("priceRange_1.0-1.2"));
        assertThat(categorizer.getCategory(2.0d), is("priceRange_2.0-2.5"));
        assertThat(categorizer.getCategory(2.3d), is("priceRange_2.0-2.5"));
        assertThat(categorizer.getCategory(2.5d), is("priceRange_2.5-3.0"));
        assertThat(categorizer.getCategory(2.9d), is("priceRange_2.5-3.0"));
        assertThat(categorizer.getCategory(4.4d), is("priceRange_4.0-5.0"));
        assertThat(categorizer.getCategory(5.5d), is("priceRange_5.0+"));
    }
}
