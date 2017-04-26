package cz.fb.manaus.core.model;

import cz.fb.manaus.core.MarketCategories;
import cz.fb.manaus.core.category.Category;
import cz.fb.manaus.core.category.categorizer.CountryCodeCategorizer;
import org.junit.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ProfitRecordTest {

    public static final String COUNTRY_PREFIX = Category.MARKET_PREFIX + CountryCodeCategorizer.PREFIX;

    @Test
    public void testLayAllPredicate() throws Exception {
        ProfitRecord cze = new ProfitRecord(COUNTRY_PREFIX + "cze", 100d, 1, 1, 2d, 0.06);
        ProfitRecord all = new ProfitRecord(MarketCategories.ALL, 100d, 1, 1, 2d, 0.06);
        assertThat(asList(cze, all).stream().filter(ProfitRecord::isAllCategory).count(), is(1L));
        assertThat(singletonList(all).stream().filter(ProfitRecord::isAllCategory).count(), is(1L));
        assertThat(singletonList(cze).stream().filter(ProfitRecord::isAllCategory).count(), is(0L));
    }


}
