package cz.fb.manaus.ischia.proposer

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.ischia.BackUniverse
import cz.fb.manaus.ischia.LayUniverse
import cz.fb.manaus.reactor.betting.proposer.FixedDowngradeStrategy
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.proposer.common.FairnessProposer
import cz.fb.manaus.reactor.price.PriceService
import org.springframework.stereotype.Component

@Component
@LayUniverse
@BackUniverse
class FairnessLayProposer(priceService: PriceService)
    : PriceProposer by FairnessProposer(
        Side.LAY,
        priceService,
        FixedDowngradeStrategy(Side.LAY, 0.077, matchOddsPredicate),
        FixedDowngradeStrategy(Side.LAY, 0.087),
        FixedDowngradeStrategy(Side.BACK, 0.067, matchOddsPredicate),
        FixedDowngradeStrategy(Side.BACK, 0.077)
)
