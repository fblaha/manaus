package cz.fb.manaus.core.model;

import cz.fb.manaus.core.MarketCategories;
import cz.fb.manaus.core.category.Category;
import cz.fb.manaus.core.category.categorizer.CountryCodeCategorizer;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ProfitRecordTest {

    public static final String COUNTRY_PREFIX = Category.MARKET_PREFIX + CountryCodeCategorizer.PREFIX;

    @Test
    public void testLayAllPredicate() {
        var cze = new ProfitRecord(COUNTRY_PREFIX + "cze", 100d, 1, 1, 2d, 0.06);
        var all = new ProfitRecord(MarketCategories.ALL, 100d, 1, 1, 2d, 0.06);
        assertThat(List.of(cze, all).stream().filter(ProfitRecord::isAllCategory).count(), is(1L));
        assertThat(List.of(all).stream().filter(ProfitRecord::isAllCategory).count(), is(1L));
        assertThat(List.of(cze).stream().filter(ProfitRecord::isAllCategory).count(), is(0L));
    }


}
