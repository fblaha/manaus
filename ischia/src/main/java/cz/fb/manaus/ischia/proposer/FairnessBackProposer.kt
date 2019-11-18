package cz.fb.manaus.ischia.proposer

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.ischia.BackLoserBet
import cz.fb.manaus.ischia.LayLoserBet
import cz.fb.manaus.reactor.betting.proposer.DowngradeStrategy
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.proposer.common.FairnessProposer
import cz.fb.manaus.reactor.price.PriceService
import org.springframework.stereotype.Component

@Component
@LayLoserBet
@BackLoserBet
class FairnessBackProposer(priceService: PriceService, vararg downgradeStrategy: DowngradeStrategy)
    : PriceProposer by FairnessProposer(Side.BACK, priceService, *downgradeStrategy)