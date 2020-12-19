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
object FairnessBackProposer : PriceProposer by FairnessProposer(
        Side.BACK,
        { backStrategy(it.side, it.version) }
)

private fun backStrategy(side: Side, version: Int) = when (side) {
    Side.BACK -> when (version) {
        1 -> 0.075
        in 2..3 -> 0.07
        in 4..7 -> 0.065
        else -> 0.06
    }
    Side.LAY -> when (version) {
        1 -> 0.087
        in 2..3 -> 0.082
        in 4..7 -> 0.077
        else -> 0.072
    }
}
