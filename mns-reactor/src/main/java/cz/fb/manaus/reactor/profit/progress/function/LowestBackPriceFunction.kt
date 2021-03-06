package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.Side
import org.springframework.stereotype.Component
import kotlin.math.min

@Component
object LowestBackPriceFunction : ProgressFunction by PriceReduceFunction(Side.BACK, { a, b -> min(a, b) })
