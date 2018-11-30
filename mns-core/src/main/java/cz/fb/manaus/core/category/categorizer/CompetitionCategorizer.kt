package cz.fb.manaus.core.category.categorizer

import com.google.common.base.CharMatcher
import cz.fb.manaus.core.model.Market
import org.springframework.stereotype.Component

@Component
class CompetitionCategorizer : AbstractDelegatingCategorizer(PREFIX) {

    override fun getCategoryRaw(market: Market): Set<String> {
        val competition = market.competition
        return if (competition == null || competition.name.isBlank()) {
            setOf("none")
        } else {
            setOf(normalize(competition.name))
        }
    }


    companion object {
        const val PREFIX = "competition_"
    }
}

private fun normalize(name: String): String {
    var result = name
    result = CharMatcher.whitespace().or(CharMatcher.javaLetterOrDigit()).retainFrom(result)
    result = CharMatcher.whitespace().replaceFrom(result, '_')
    result = result.substring(0, Math.min(result.length, 30))
    return result
}
