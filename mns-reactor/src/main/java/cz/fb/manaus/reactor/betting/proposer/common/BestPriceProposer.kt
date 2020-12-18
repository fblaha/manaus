package cz.fb.manaus.reactor.betting.proposer.common

import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.validator.ValidationResult

class BestPriceProposer(
        private val step: Double
) : PriceProposer {

    override fun validate(event: BetEvent): ValidationResult {
        val runnerPrices = event.runnerPrices
        val homogeneous = runnerPrices.getHomogeneous(event.side.opposite)
        val bestPrice = homogeneous.bestPrice
        return if (bestPrice != null) ValidationResult.OK else ValidationResult.DROP
    }

    override fun getProposedPrice(event: BetEvent): Double? {
        val side = event.side
        val bestPrice = event.runnerPrices.getHomogeneous(side.opposite).bestPrice!!.price
        check(step >= 0)
        return if (step == 0.0) {
            bestPrice
        } else {
            if (side == Side.LAY) {
                Price.round(bestPrice * (1.0 + step))
            } else {
                Price.round(bestPrice * (1.0 - step))
            }
        }
    }
}
