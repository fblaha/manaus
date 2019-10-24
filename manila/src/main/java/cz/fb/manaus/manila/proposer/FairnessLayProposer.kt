package cz.fb.manaus.manila.proposer

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.manila.ManilaBet
import cz.fb.manaus.reactor.betting.proposer.FixedDowngradeStrategy
import cz.fb.manaus.reactor.betting.proposer.common.AbstractFairnessProposer
import org.springframework.stereotype.Component

@Component
@ManilaBet
object FairnessLayProposer : AbstractFairnessProposer(Side.LAY, FixedDowngradeStrategy(0.07, 0.07))
