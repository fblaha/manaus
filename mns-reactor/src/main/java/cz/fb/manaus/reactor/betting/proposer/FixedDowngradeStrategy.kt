package cz.fb.manaus.reactor.betting.proposer

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.provider.ProviderTag
import cz.fb.manaus.reactor.betting.BetContext

class FixedDowngradeStrategy(val back: Double, val lay: Double, override val tags: Set<ProviderTag> = emptySet()) : DowngradeStrategy {

    override fun invoke(ctx: BetContext): Double {
        return when (ctx.side) {
            Side.BACK -> back
            Side.LAY -> lay
        }
    }
}