package cz.fb.manaus.reactor.betting.proposer

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.provider.ProviderTag
import cz.fb.manaus.reactor.betting.BetEvent

class FixedDowngradeStrategy(val back: Double, val lay: Double, override val tags: Set<ProviderTag> = emptySet()) : DowngradeStrategy {

    override fun invoke(event: BetEvent): Double {
        return when (event.side) {
            Side.BACK -> back
            Side.LAY -> lay
        }
    }
}