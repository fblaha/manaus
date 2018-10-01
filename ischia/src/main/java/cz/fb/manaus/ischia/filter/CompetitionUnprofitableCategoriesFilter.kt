package cz.fb.manaus.ischia.filter

import cz.fb.manaus.core.category.Category
import cz.fb.manaus.core.category.categorizer.CompetitionCategorizer
import cz.fb.manaus.ischia.BackLoserBet
import cz.fb.manaus.ischia.LayLoserBet
import cz.fb.manaus.reactor.filter.AbstractUnprofitableCategoriesRegistry
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Duration

@LayLoserBet
@BackLoserBet
@Component
@Profile(ManausProfiles.DB)
open class CompetitionUnprofitableCategoriesFilter protected constructor() : AbstractUnprofitableCategoriesRegistry("competition", Duration.ofDays(120), null, -30.0, PREFIX, mapOf(20 to 2, 15 to 2, 10 to 2)) {
    companion object {
        const val PREFIX = Category.MARKET_PREFIX + CompetitionCategorizer.PREFIX
    }
}
