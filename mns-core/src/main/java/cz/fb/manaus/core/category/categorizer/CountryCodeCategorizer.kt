package cz.fb.manaus.core.category.categorizer

import cz.fb.manaus.core.model.Market
import org.springframework.stereotype.Component

const val COUNTRY_PREFIX = "country_"

@Component
object CountryCodeCategorizer : AbstractDelegatingCategorizer(COUNTRY_PREFIX) {

    override fun getCategoryRaw(market: Market): Set<String> {
        return when (val countryCode = market.event.countryCode) {
            null -> emptySet()
            else -> setOf(countryCode.toLowerCase())
        }
    }
}
