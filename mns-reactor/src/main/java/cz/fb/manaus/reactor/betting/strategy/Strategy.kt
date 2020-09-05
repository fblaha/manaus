package cz.fb.manaus.reactor.betting.strategy

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.BetEvent

typealias Strategy = (BetEvent) -> Double?

typealias BetEventPredicate = (BetEvent) -> Boolean


fun chain(vararg strategies: Strategy): Strategy {
    val sequence = strategies.toList().asSequence()
    return { event -> sequence.mapNotNull { it(event) }.firstOrNull() }
}

fun fixedStrategy(
        side: Side,
        value: Double,
        predicate: BetEventPredicate = { true }
): Strategy =
        { if (side == it.side && predicate(it)) value else null }
