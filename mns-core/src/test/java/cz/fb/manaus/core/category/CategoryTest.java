package cz.fb.manaus.core.category;

import cz.fb.manaus.core.category.categorizer.CountryCodeCategorizer;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class CategoryTest {

    public static final String CZE = "cze";
    public static final String RAW = CountryCodeCategorizer.PREFIX + CZE;
    public static final String MARKET_CZE = Category.MARKET_PREFIX + RAW;
    public static final String COUNTRY_CAT_BASE = Category.MARKET_PREFIX + CountryCodeCategorizer.PREFIX;


    @Test
    public void testBaseTail() throws Exception {
        assertThat(Category.parse(MARKET_CZE).getBase(), is(COUNTRY_CAT_BASE));
        assertThat(Category.parse(MARKET_CZE).getTail(), is(CZE));
        assertThat(Category.parse(RAW).getTail(), is(CZE));
    }

    @Test
    public void testBaseMultiPart() throws Exception {
        assertThat(Category.parse(MARKET_CZE + "_kl").getBase(), is(COUNTRY_CAT_BASE));
    }

}
