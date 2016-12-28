package cz.fb.manaus.core.category.categorizer;

import cz.fb.manaus.core.test.AbstractLocalTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class MarketRegexpCategorizerTest extends AbstractLocalTestCase {

    @Autowired
    private MarketRegexpCategorizer categorizer;

    @Test
    public void testOverUnder() throws Exception {
        assertThat(categorizer.getCategories("Over/Under 2.5 goals", "event"), hasItem("market_regexp_overUnderGoals"));
    }

    @Test
    public void testEvents() throws Exception {
        assertThat(categorizer.getCategories("-", "Czech Rep U19 v Moldova U19"), hasItems("market_regexp_underAge", "market_regexp_underAge_19"));
        assertThat(categorizer.getCategories("-", "Roa IL (W) v Arna Bjornar (W)"), hasItem("market_regexp_women"));
        assertThat(categorizer.getCategories("-", "Roa IL (W) v Arna Bjornar (W) x"), hasItem("market_regexp_women"));
        assertThat(categorizer.getCategories("-", "Roa IL (W) v Arna Bjornar (W)x"), not(hasItem("market_regexp_women")));
        assertThat(categorizer.getCategories("-", "Kocaelispor (Res) v Fenerbahce (Res)"), hasItem("market_regexp_reserveTeam"));
    }

}
