package cz.fb.manaus.reactor.betting.proposer

import cz.fb.manaus.reactor.betting.BetContext

class FixedDowngradeStrategy(val value: Double) : DowngradeStrategy {
    override fun invoke(ctx: BetContext): Double {
        return value
    }
}