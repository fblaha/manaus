package cz.fb.manaus.ischia.proposer

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.ischia.BackUniverse
import cz.fb.manaus.ischia.LayUniverse
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.proposer.common.FairnessProposer
import cz.fb.manaus.reactor.price.PriceService
import org.springframework.stereotype.Component

@Component
@LayUniverse
@BackUniverse
class FairnessLayProposer(priceService: PriceService) : PriceProposer by FairnessProposer(
        Side.LAY,
        priceService,
        { layStrategy(it.side, it.version) }
)


fun layStrategy(side: Side, version: Int): Double? = when (side) {
    Side.BACK -> when (version) {
        1 -> 0.08
        in 2..3 -> 0.075
        in 4..7 -> 0.07
        else -> 0.065
    }
    Side.LAY -> when (version) {
        1 -> 0.09
        in 2..3 -> 0.085
        in 4..7 -> 0.08
        else -> 0.075
    }
}
