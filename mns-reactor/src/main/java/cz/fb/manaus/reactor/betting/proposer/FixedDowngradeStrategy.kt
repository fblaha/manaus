package cz.fb.manaus.reactor.betting.proposer

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.BetEvent

fun fixedDowngradeStrategy(side: Side, value: Double, predicate: (BetEvent) -> Boolean = { true }): DowngradeStrategy {
    return { if (side == it.side && predicate(it)) value else null }
}