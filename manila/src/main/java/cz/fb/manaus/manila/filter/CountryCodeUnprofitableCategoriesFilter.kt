package cz.fb.manaus.manila.filter

import cz.fb.manaus.core.category.Category
import cz.fb.manaus.core.category.categorizer.COUNTRY_PREFIX
import cz.fb.manaus.manila.ManilaBet
import cz.fb.manaus.reactor.filter.AbstractUnprofitableCategoriesRegistry
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Duration

@ManilaBet
@Component
@Profile(ManausProfiles.DB)
object CountryCodeUnprofitableCategoriesFilter : AbstractUnprofitableCategoriesRegistry("countryCodeLay",
        Duration.ofDays(30), null, -30.0, Category.MARKET_PREFIX + COUNTRY_PREFIX, mapOf(20 to 3))
