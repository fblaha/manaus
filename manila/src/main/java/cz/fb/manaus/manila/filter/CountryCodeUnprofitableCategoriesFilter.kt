package cz.fb.manaus.manila.filter

import cz.fb.manaus.core.batch.RealizedBetLoader
import cz.fb.manaus.core.category.Category
import cz.fb.manaus.core.category.categorizer.COUNTRY_PREFIX
import cz.fb.manaus.core.repository.SettledBetRepository
import cz.fb.manaus.manila.ManilaBet
import cz.fb.manaus.reactor.filter.BlacklistSupplier
import cz.fb.manaus.reactor.filter.UnprofitableCategoriesRegistry
import cz.fb.manaus.reactor.profit.ProfitService
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Duration

@ManilaBet
@Component
@Profile(ManausProfiles.DB)
class CountryCodeUnprofitableCategoriesFilter(
        profitService: ProfitService,
        settledBetRepository: SettledBetRepository,
        realizedBetLoader: RealizedBetLoader
) : BlacklistSupplier by UnprofitableCategoriesRegistry(
        name = "countryCodeLay",
        period = Duration.ofDays(30),
        maximalProfit = -30.0,
        filterPrefix = Category.MARKET_PREFIX + COUNTRY_PREFIX,
        thresholds = mapOf(20 to 3),
        profitService = profitService,
        settledBetRepository = settledBetRepository,
        realizedBetLoader = realizedBetLoader
)
