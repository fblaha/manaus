package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.model.Side
import org.springframework.stereotype.Component

@Component
class LayPriceCountCategorizer : AbstractPriceCountCategorizer("priceCountLay_", 3, Side.LAY)