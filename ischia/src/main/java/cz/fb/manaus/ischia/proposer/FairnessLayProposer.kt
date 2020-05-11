package cz.fb.manaus.ischia.proposer

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.ischia.BackUniverse
import cz.fb.manaus.ischia.LayUniverse
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.proposer.common.FairnessProposer
import cz.fb.manaus.reactor.betting.proposer.fixedDowngradeStrategy
import cz.fb.manaus.reactor.price.PriceService
import org.springframework.stereotype.Component

@Component
@LayUniverse
@BackUniverse
class FairnessLayProposer(priceService: PriceService)
    : PriceProposer by FairnessProposer(
        Side.LAY,
        priceService,
        fixedDowngradeStrategy(Side.LAY, 0.077, ::isMatchOdds),
        fixedDowngradeStrategy(Side.LAY, 0.087),
        fixedDowngradeStrategy(Side.BACK, 0.067, ::isMatchOdds),
        fixedDowngradeStrategy(Side.BACK, 0.077)
)
