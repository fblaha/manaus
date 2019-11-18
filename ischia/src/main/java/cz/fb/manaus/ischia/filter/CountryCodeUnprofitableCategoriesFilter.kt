package cz.fb.manaus.ischia.filter

import cz.fb.manaus.core.category.Category
import cz.fb.manaus.core.category.categorizer.COUNTRY_PREFIX
import cz.fb.manaus.core.repository.RealizedBetLoader
import cz.fb.manaus.core.repository.SettledBetRepository
import cz.fb.manaus.ischia.BackLoserBet
import cz.fb.manaus.ischia.LayLoserBet
import cz.fb.manaus.reactor.filter.BlacklistSupplier
import cz.fb.manaus.reactor.filter.UnprofitableCategoriesRegistry
import cz.fb.manaus.reactor.profit.ProfitService
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Duration

@LayLoserBet
@BackLoserBet
@Component
@Profile(ManausProfiles.DB)
class CountryCodeUnprofitableCategoriesFilter(
        profitService: ProfitService,
        settledBetRepository: SettledBetRepository,
        realizedBetLoader: RealizedBetLoader
) : BlacklistSupplier by UnprofitableCategoriesRegistry(
        name = "countryCodeLay",
        period = Duration.ofDays(30),
        maximalProfit = -45.0,
        filterPrefix = Category.MARKET_PREFIX + COUNTRY_PREFIX,
        thresholds = mapOf(20 to 3),
        profitService = profitService,
        settledBetRepository = settledBetRepository,
        realizedBetLoader = realizedBetLoader
)