package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.Side
import org.springframework.stereotype.Component

@Component
class LowestBackPriceFunction : AbstractPriceReduceFunction(Side.BACK, { a, b -> Math.min(a, b) })
