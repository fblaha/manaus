package cz.fb.manaus.core.category

import cz.fb.manaus.core.MarketCategories
import java.util.regex.Matcher
import java.util.regex.Pattern

data class Category(private val category: String) {

    val isAll: Boolean
        get() = MarketCategories.ALL == category

    val base: String
        get() = matcher.group(1)


    val tail: String
        get() = matcher.group(2)

    private val matcher: Matcher
        get() {
            val matcher = PATTERN.matcher(category)
            check(matcher.matches())
            check(MARKET_PREFIX != matcher.group(1))
            return matcher
        }

    companion object {
        val PATTERN = Pattern.compile("^((?:market_)?[a-zA-Z0-9]+_)(.*)")!!
        const val MARKET_PREFIX = "market_"

        fun parse(category: String): Category {
            return Category(category)
        }
    }
}
