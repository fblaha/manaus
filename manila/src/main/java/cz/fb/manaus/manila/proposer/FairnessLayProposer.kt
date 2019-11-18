package cz.fb.manaus.manila.proposer

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.manila.ManilaBet
import cz.fb.manaus.reactor.betting.proposer.FixedDowngradeStrategy
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.proposer.common.FairnessProposer
import cz.fb.manaus.reactor.price.PriceService
import org.springframework.stereotype.Component

@Component
@ManilaBet
class FairnessLayProposer(priceService: PriceService)
    : PriceProposer by FairnessProposer(Side.LAY, priceService, FixedDowngradeStrategy(0.07, 0.07))
