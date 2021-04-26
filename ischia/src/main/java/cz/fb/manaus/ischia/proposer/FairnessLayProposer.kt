package cz.fb.manaus.ischia.proposer

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.ischia.BackUniverse
import cz.fb.manaus.ischia.LayUniverse
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.proposer.common.FairnessProposer
import org.springframework.stereotype.Component

@Component
@LayUniverse
@BackUniverse
object FairnessLayProposer : PriceProposer by FairnessProposer(
        Side.LAY,
        { layStrategy(it.side, it.version) }
)


fun layStrategy(side: Side, version: Int): Double = when (side) {
    Side.BACK -> when (version) {
        1 -> 0.075
        in 2..3 -> 0.070
        in 4..7 -> 0.065
        else -> 0.055
    }
    Side.LAY -> when (version) {
        1 -> 0.080
        in 2..3 -> 0.075
        in 4..7 -> 0.070
        else -> 0.065
    }
}
