package cz.fb.manaus.reactor.betting.proposer

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.provider.ProviderTag
import cz.fb.manaus.reactor.betting.BetEvent

class FixedDowngradeStrategy(
        private val side: Side,
        private val value: Double,
        private val predicate: (BetEvent) -> Boolean = { true },
        override val tags: Set<ProviderTag> = emptySet()
) : DowngradeStrategy {

    override fun invoke(event: BetEvent): Double? {
        return if (side == event.side && predicate(event)) value else null
    }

}