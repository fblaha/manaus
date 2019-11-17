package cz.fb.manaus.manila.proposer

import cz.fb.manaus.manila.ManilaBet
import cz.fb.manaus.reactor.betting.proposer.common.AbstractBestPriceProposer
import cz.fb.manaus.reactor.rounding.RoundingService
import org.springframework.stereotype.Component

@Component
@ManilaBet
class BestPriceProposer(roundingService: RoundingService) : AbstractBestPriceProposer(1, roundingService)
