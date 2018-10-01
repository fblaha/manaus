package cz.fb.manaus.core.category.categorizer

import cz.fb.manaus.core.model.Market
import org.springframework.stereotype.Component

@Component
class CountryCodeCategorizer : AbstractDelegatingCategorizer(PREFIX) {

    override fun getCategoryRaw(market: Market): Set<String> {
        val countryCode = market.event.countryCode
        return setOf(countryCode?.toLowerCase() ?: "none")
    }

    companion object {
        const val PREFIX = "country_"
    }
}
