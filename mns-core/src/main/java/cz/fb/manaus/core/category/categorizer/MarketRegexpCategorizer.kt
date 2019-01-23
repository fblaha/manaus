package cz.fb.manaus.core.category.categorizer

import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.Category
import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.model.RealizedBet
import org.springframework.stereotype.Component

val MATCH_MAP = mapOf(
        "overUnderGoals" to compile("^Over/Under\\s+\\d+\\.5\\s+goals$"),
        "regularTimeMatchOdd" to compile("^Regular\\s+Time\\s+Match\\s+Odds$"))
val EVENT_MAP = mapOf(
        "underAge" to compile("^.*\\s+U[12]\\d\\s+.*\\s+U[12]\\d(?:\\s+.*)?$"),
        "underAge_{1}" to compile("^.*\\s+U([12]\\d)\\s+.*\\s+U[12]\\d(?:\\s+.*)?$"),
        "women" to compile("^.*\\s+\\(w\\)\\s+.*\\s+\\(w\\)(?:\\s+.*)?$"),
        "reserveTeam" to compile("^.*\\s+\\(Res\\)\\s+.*\\s+\\(Res\\)(?:\\s+.*)?$"))


@Component
object MarketRegexpCategorizer : AbstractRegexpResolver(Category.MARKET_PREFIX + "regexp_"), RealizedBetCategorizer, Categorizer {

    override fun getCategories(realizedBet: RealizedBet, coverage: BetCoverage): Set<String> {
        val marketName = realizedBet.market.name
        val eventName = realizedBet.market.event.name
        return getCategories(marketName, eventName)
    }

    override fun getCategories(market: Market): Set<String> {
        return getCategories(market.name, market.event.name)
    }

    internal fun getCategories(marketName: String, eventName: String): Set<String> {
        val result = mutableSetOf<String>()
        result.addAll(getCategories(marketName, MATCH_MAP).map { this.addPrefix(it) })
        result.addAll(getCategories(eventName, EVENT_MAP).map { this.addPrefix(it) })
        return result
    }
}
