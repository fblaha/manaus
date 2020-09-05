package cz.fb.manaus.ischia.proposer

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.ischia.BackUniverse
import cz.fb.manaus.ischia.LayUniverse
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.proposer.common.FairnessProposer
import cz.fb.manaus.reactor.betting.strategy.chain
import cz.fb.manaus.reactor.betting.strategy.fixedStrategy
import cz.fb.manaus.reactor.price.PriceService
import org.springframework.stereotype.Component

fun isDraw(e: BetEvent): Boolean {
    val (_, name, _, _) = e.runner
    return "draw" in name.toLowerCase()
}


@Component
@LayUniverse
@BackUniverse
class FairnessBackProposer(priceService: PriceService) : PriceProposer by FairnessProposer(
        Side.BACK,
        priceService,
        chain(
                fixedStrategy(Side.LAY, 0.077, ::isDraw),
                fixedStrategy(Side.LAY, 0.09),

                fixedStrategy(Side.BACK, 0.06, ::isDraw),
                fixedStrategy(Side.BACK, 0.08)
        )
)
