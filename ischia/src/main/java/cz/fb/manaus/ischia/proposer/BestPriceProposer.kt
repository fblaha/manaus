package cz.fb.manaus.ischia.proposer

import cz.fb.manaus.ischia.BackLoserBet
import cz.fb.manaus.ischia.LayLoserBet
import cz.fb.manaus.reactor.betting.proposer.common.AbstractBestPriceProposer
import cz.fb.manaus.reactor.rounding.RoundingService
import org.springframework.stereotype.Component

@Component
@BackLoserBet
@LayLoserBet
class BestPriceProposer(roundingService: RoundingService) : AbstractBestPriceProposer(1, roundingService)
