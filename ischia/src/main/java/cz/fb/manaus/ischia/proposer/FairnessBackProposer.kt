package cz.fb.manaus.ischia.proposer

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.model.TYPE_MATCH_ODDS
import cz.fb.manaus.ischia.BackUniverse
import cz.fb.manaus.ischia.LayUniverse
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.proposer.FixedDowngradeStrategy
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.proposer.common.FairnessProposer
import cz.fb.manaus.reactor.price.PriceService
import org.springframework.stereotype.Component

val matchOddsPredicate: (BetEvent) -> Boolean = { TYPE_MATCH_ODDS == it.market.type }

@Component
@LayUniverse
@BackUniverse
class FairnessBackProposer(priceService: PriceService)
    : PriceProposer by FairnessProposer(
        Side.BACK,
        priceService,
        FixedDowngradeStrategy(Side.LAY, 0.077, matchOddsPredicate),
        FixedDowngradeStrategy(Side.LAY, 0.087),
        FixedDowngradeStrategy(Side.BACK, 0.07, matchOddsPredicate),
        FixedDowngradeStrategy(Side.BACK, 0.08)
)
