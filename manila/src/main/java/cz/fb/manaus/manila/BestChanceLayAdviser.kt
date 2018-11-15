package cz.fb.manaus.manila

import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.proposer.ProposerAdviser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@ManilaBet
@Component
class BestChanceLayAdviser @ManilaBet @Autowired constructor(proposers: List<PriceProposer>)
    : ProposerAdviser(proposers)
