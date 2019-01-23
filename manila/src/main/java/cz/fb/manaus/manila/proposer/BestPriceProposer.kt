package cz.fb.manaus.manila.proposer

import cz.fb.manaus.manila.ManilaBet
import cz.fb.manaus.reactor.betting.proposer.common.AbstractBestPriceProposer
import org.springframework.stereotype.Component

@Component
@ManilaBet
object BestPriceProposer : AbstractBestPriceProposer(1)
