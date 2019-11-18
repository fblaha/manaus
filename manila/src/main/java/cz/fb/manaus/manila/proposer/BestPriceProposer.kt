package cz.fb.manaus.manila.proposer

import cz.fb.manaus.manila.ManilaBet
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.proposer.common.BestPriceProposer
import cz.fb.manaus.reactor.rounding.RoundingService
import org.springframework.stereotype.Component

@Component
@ManilaBet
class BestPriceProposer(roundingService: RoundingService)
    : PriceProposer by BestPriceProposer(1, roundingService)
