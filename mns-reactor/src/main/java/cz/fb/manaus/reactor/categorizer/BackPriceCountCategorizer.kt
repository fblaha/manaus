package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.repository.domain.Side
import org.springframework.stereotype.Component

@Component
class BackPriceCountCategorizer : AbstractPriceCountCategorizer("priceCountBack_", 3, Side.BACK)
