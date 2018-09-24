package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.Side
import org.springframework.stereotype.Component

@Component
class HighestLayPriceFunction : AbstractPriceReduceFunction(Side.LAY, { a, b -> Math.max(a, b) })