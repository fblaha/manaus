package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.Side
import org.springframework.stereotype.Component
import kotlin.math.max

@Component
object HighestBackPriceFunction : ProgressFunction by PriceReduceFunction(Side.BACK, { a, b -> max(a, b) })
