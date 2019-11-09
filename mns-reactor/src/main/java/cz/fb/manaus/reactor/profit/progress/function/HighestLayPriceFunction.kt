package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.Side
import org.springframework.stereotype.Component
import kotlin.math.max

@Component
object HighestLayPriceFunction : ProgressFunction by PriceReduceFunction(Side.LAY, { a, b -> max(a, b) })
