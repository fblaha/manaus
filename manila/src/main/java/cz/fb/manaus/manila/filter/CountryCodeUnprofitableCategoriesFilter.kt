package cz.fb.manaus.manila.filter

import cz.fb.manaus.core.category.Category
import cz.fb.manaus.core.category.categorizer.CountryCodeCategorizer
import cz.fb.manaus.manila.ManilaBet
import cz.fb.manaus.reactor.filter.AbstractUnprofitableCategoriesRegistry
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Duration

@ManilaBet
@Component
@Profile(ManausProfiles.DB)
class CountryCodeUnprofitableCategoriesFilter protected constructor() : AbstractUnprofitableCategoriesRegistry("countryCodeLay",
        Duration.ofDays(30), null, -30.0, PREFIX, mapOf(20 to 3)) {
    companion object {
        const val PREFIX = Category.MARKET_PREFIX + CountryCodeCategorizer.PREFIX
    }
}
