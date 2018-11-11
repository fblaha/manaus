package cz.fb.manaus.core.category.categorizer

import cz.fb.manaus.core.repository.domain.Market
import org.springframework.stereotype.Component

@Component
class CountryCodeCategorizer : AbstractDelegatingCategorizer(PREFIX) {

    override fun getCategoryRaw(market: Market): Set<String> {
        val countryCode = market.event.countryCode
        return if (countryCode == null) emptySet() else setOf(countryCode.toLowerCase())
    }

    companion object {
        const val PREFIX = "country_"
    }
}
