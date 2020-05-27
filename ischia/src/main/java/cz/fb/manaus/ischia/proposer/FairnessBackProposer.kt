package cz.fb.manaus.ischia.proposer

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.ischia.BackUniverse
import cz.fb.manaus.ischia.LayUniverse
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.proposer.chain
import cz.fb.manaus.reactor.betting.proposer.common.FairnessProposer
import cz.fb.manaus.reactor.betting.proposer.fixedDowngradeStrategy
import cz.fb.manaus.reactor.price.PriceService
import org.springframework.stereotype.Component

fun isDraw(e: BetEvent): Boolean {
    val (_, name, _, _) = e.runner
    return "draw" in name.toLowerCase()
}


@Component
@LayUniverse
@BackUniverse
class FairnessBackProposer(priceService: PriceService)
    : PriceProposer by FairnessProposer(
        Side.BACK,
        priceService,
        chain(
                fixedDowngradeStrategy(Side.LAY, 0.077, ::isDraw),
                fixedDowngradeStrategy(Side.LAY, 0.1),

                fixedDowngradeStrategy(Side.BACK, 0.07, ::isDraw),
                fixedDowngradeStrategy(Side.BACK, 0.09)
        )
)
