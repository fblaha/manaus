package cz.fb.manaus.core.model;

import cz.fb.manaus.core.MarketCategories;
import cz.fb.manaus.core.category.Category;
import cz.fb.manaus.core.category.categorizer.CountryCodeCategorizer;
import org.junit.Test;

import java.util.NoSuchElementException;

import static com.google.common.collect.Iterables.find;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ProfitRecordTest {

    public static final String COUNTRY_PREFIX = Category.MARKET_PREFIX + CountryCodeCategorizer.PREFIX;

    @Test
    public void testLayAllPredicate() throws Exception {
        ProfitRecord cze = new ProfitRecord(COUNTRY_PREFIX + "cze", 100d, 1, 1, 2d, 0.06);
        ProfitRecord all = new ProfitRecord(MarketCategories.ALL, 100d, 1, 1, 2d, 0.06);
        assertThat(find(asList(cze, all), ProfitRecord::isAllCategory), notNullValue());
        assertThat(find(singletonList(all), ProfitRecord::isAllCategory), notNullValue());
        try {
            find(singletonList(cze), ProfitRecord::isAllCategory);
            fail();
        } catch (NoSuchElementException e) {
            System.out.println("OK: " + e);
        }
    }


}
