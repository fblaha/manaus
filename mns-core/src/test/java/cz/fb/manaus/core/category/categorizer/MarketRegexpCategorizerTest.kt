package cz.fb.manaus.core.category.categorizer

import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class MarketRegexpCategorizerTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var categorizer: MarketRegexpCategorizer

    @Test
    fun `over-under market type`() {
        assertThat(categorizer.getCategories("Over/Under 2.5 goals", "event"), hasItem("market_regexp_overUnderGoals"))
    }

    @Test
    fun `categories based on events`() {
        assertThat(categorizer.getCategories("-", "Czech Rep U19 v Moldova U19"), hasItems("market_regexp_underAge", "market_regexp_underAge_19"))
        assertThat(categorizer.getCategories("-", "Roa IL (W) v Arna Bjornar (W)"), hasItem("market_regexp_women"))
        assertThat(categorizer.getCategories("-", "Roa IL (W) v Arna Bjornar (W) x"), hasItem("market_regexp_women"))
        assertThat(categorizer.getCategories("-", "Roa IL (W) v Arna Bjornar (W)x"), not(hasItem("market_regexp_women")))
        assertThat(categorizer.getCategories("-", "Kocaelispor (Res) v Fenerbahce (Res)"), hasItem("market_regexp_reserveTeam"))
    }

}
