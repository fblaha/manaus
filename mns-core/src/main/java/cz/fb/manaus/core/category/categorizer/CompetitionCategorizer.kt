package cz.fb.manaus.core.category.categorizer

import cz.fb.manaus.core.model.Market
import org.springframework.stereotype.Component

@Component
class CompetitionCategorizer : AbstractDelegatingCategorizer(PREFIX) {

    override fun getCategoryRaw(market: Market): Set<String> {
        val competition = market.competition
        return if (competition == null || competition.name.isBlank()) {
            setOf("none")
        } else {
            setOf(sanitize(competition.name))
        }
    }

    companion object {
        const val PREFIX = "competition_"
    }
}

private fun sanitize(name: String): String {
    return name.filter { it.isLetterOrDigit() || it.isWhitespace() }
        .replace("\\s".toRegex(), "_")
        .take(30)
}
