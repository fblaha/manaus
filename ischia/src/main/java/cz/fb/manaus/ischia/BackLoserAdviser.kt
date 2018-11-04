package cz.fb.manaus.ischia

import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.proposer.ProposerAdviser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@BackLoserBet
@Component
class BackLoserAdviser @BackLoserBet @Autowired constructor(proposers: List<PriceProposer>) : ProposerAdviser(proposers)
