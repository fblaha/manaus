package cz.fb.manaus.core.category.categorizer

import com.google.common.base.CharMatcher
import com.google.common.base.Strings
import cz.fb.manaus.core.model.Market
import org.springframework.stereotype.Component

@Component
class CompetitionCategorizer : AbstractDelegatingCategorizer(PREFIX) {

    public override fun getCategoryRaw(market: Market): Set<String> {
        val competition = market.competition
        return if (competition == null || Strings.isNullOrEmpty(competition.name)) {
            setOf("none")
        } else {
            setOf(normalize(competition.name))
        }
    }

    private fun normalize(name: String): String {
        var name = name
        name = CharMatcher.whitespace().or(CharMatcher.javaLetterOrDigit()).retainFrom(name)
        name = CharMatcher.whitespace().replaceFrom(name, '_')
        name = name.substring(0, Math.min(name.length, 30))
        return name
    }

    companion object {
        const val PREFIX = "competition_"
    }
}
