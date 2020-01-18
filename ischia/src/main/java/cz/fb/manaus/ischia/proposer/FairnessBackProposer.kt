package cz.fb.manaus.ischia.proposer

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.ischia.BackUniverse
import cz.fb.manaus.ischia.LayUniverse
import cz.fb.manaus.reactor.betting.proposer.DowngradeStrategy
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.proposer.common.FairnessProposer
import cz.fb.manaus.reactor.price.PriceService
import org.springframework.stereotype.Component

@Component
@LayUniverse
@BackUniverse
class FairnessBackProposer(priceService: PriceService, @BackUniverse vararg downgradeStrategy: DowngradeStrategy)
    : PriceProposer by FairnessProposer(Side.BACK, priceService, *downgradeStrategy)