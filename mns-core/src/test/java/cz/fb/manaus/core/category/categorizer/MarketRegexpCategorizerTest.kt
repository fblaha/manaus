package cz.fb.manaus.core.category.categorizer

import cz.fb.manaus.core.test.AbstractTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertTrue

class MarketRegexpCategorizerTest : AbstractTestCase() {

    @Autowired
    private lateinit var categorizer: MarketRegexpCategorizer

    @Test
    fun `over-under market type`() {
        assertTrue { "market_regexp_overUnderGoals" in categorizer.getCategories("Over/Under 2.5 goals", "event") }
    }

    @Test
    fun `categories based on events`() {
        assertTrue { "market_regexp_underAge" in categorizer.getCategories("-", "Czech Rep U19 v Moldova U19") }
        assertTrue { "market_regexp_underAge_19" in categorizer.getCategories("-", "Czech Rep U19 v Moldova U19") }
        assertTrue { "market_regexp_women" in categorizer.getCategories("-", "Roa IL (W) v Arna Bjornar (W)") }
        assertTrue {
            "market_regexp_reserveTeam" in categorizer.getCategories(
                "-",
                "Kocaelispor (Res) v Fenerbahce (Res)"
            )
        }
    }

}
