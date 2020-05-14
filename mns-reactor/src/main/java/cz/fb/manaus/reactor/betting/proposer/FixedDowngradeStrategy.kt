package cz.fb.manaus.reactor.betting.proposer

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.BetEvent

typealias BetEventPredicate = (BetEvent) -> Boolean


fun combine(vararg strategies: DowngradeStrategy): DowngradeStrategy {
    val sequence = strategies.toList().asSequence()
    return { event -> sequence.mapNotNull { it(event) }.firstOrNull() }
}

fun fixedDowngradeStrategy(
        side: Side,
        value: Double,
        predicate: BetEventPredicate = { true }
): DowngradeStrategy =
        { if (side == it.side && predicate(it)) value else null }
