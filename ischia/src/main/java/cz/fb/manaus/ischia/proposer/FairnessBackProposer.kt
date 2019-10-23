package cz.fb.manaus.ischia.proposer

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.ischia.BackLoserBet
import cz.fb.manaus.reactor.betting.proposer.DowngradeStrategy
import cz.fb.manaus.reactor.betting.proposer.common.AbstractFairnessProposer
import org.springframework.stereotype.Component

@Component
@BackLoserBet
class FairnessBackProposer(downgradeStrategy: DowngradeStrategy) : AbstractFairnessProposer(Side.BACK, downgradeStrategy)