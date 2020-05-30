package cz.fb.manaus.ischia.proposer

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.ischia.BackUniverse
import cz.fb.manaus.ischia.LayUniverse
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.proposer.chain
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
        chain(
                fixedDowngradeStrategy(Side.LAY, 0.077, ::isDraw),
                fixedDowngradeStrategy(Side.LAY, 0.09),

                fixedDowngradeStrategy(Side.BACK, 0.06, ::isDraw),
                fixedDowngradeStrategy(Side.BACK, 0.085)
        )
)
