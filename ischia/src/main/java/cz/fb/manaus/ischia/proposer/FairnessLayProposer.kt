package cz.fb.manaus.ischia.proposer

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.ischia.LayLoserBet
import cz.fb.manaus.reactor.betting.proposer.DowngradeStrategy
import cz.fb.manaus.reactor.betting.proposer.common.AbstractFairnessProposer
import cz.fb.manaus.reactor.price.PriceService
import org.springframework.stereotype.Component

@Component
@LayLoserBet
class FairnessLayProposer(priceService: PriceService, vararg downgradeStrategy: DowngradeStrategy)
    : AbstractFairnessProposer(Side.LAY, priceService, *downgradeStrategy)
