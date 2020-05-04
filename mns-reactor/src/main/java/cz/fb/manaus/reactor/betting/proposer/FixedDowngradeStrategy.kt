package cz.fb.manaus.reactor.betting.proposer

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.provider.ProviderTag
import cz.fb.manaus.reactor.betting.BetEvent

class FixedDowngradeStrategy(
        val back: Map<String,Double>,
        val lay: Map<String,Double>,
        override val tags: Set<ProviderTag> = emptySet()
) : DowngradeStrategy {

    override fun invoke(event: BetEvent): Double {
        val type = event.market.type ?: ""
        return when (event.side) {
            Side.BACK -> back.getValue(type)
            Side.LAY -> lay.getValue(type)
        }
    }
}