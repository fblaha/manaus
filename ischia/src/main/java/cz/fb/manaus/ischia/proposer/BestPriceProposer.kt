package cz.fb.manaus.ischia.proposer

import cz.fb.manaus.ischia.BackUniverse
import cz.fb.manaus.ischia.LayUniverse
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.proposer.common.BestPriceProposer
import org.springframework.stereotype.Component

@Component
@BackUniverse
@LayUniverse
class BestPriceProposer : PriceProposer by BestPriceProposer(0.01)
