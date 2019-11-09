package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.Side
import org.springframework.stereotype.Component
import kotlin.math.min

@Component
object LowestLayPriceFunction : ProgressFunction by PriceReduceFunction(Side.LAY, { a, b -> min(a, b) })
