package cz.fb.manaus.ischia

import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.proposer.ProposerAdviser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@LayLoserBet
@Component
class LayLoserAdviser @LayLoserBet @Autowired constructor(proposers: List<PriceProposer>) : ProposerAdviser(proposers)
