package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.Side
import org.springframework.stereotype.Component

@Component
object LowestBackPriceFunction : AbstractPriceReduceFunction(Side.BACK, { a, b -> Math.min(a, b) })
