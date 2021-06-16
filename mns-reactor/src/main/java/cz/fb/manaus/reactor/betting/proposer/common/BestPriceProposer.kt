package cz.fb.manaus.reactor.betting.proposer.common

import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.priceEq
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.price.Pricing

class BestPriceProposer(
        private val step: Double
) : PriceProposer {

    override fun validate(event: BetEvent): ValidationResult {
        val runnerPrices = event.runnerPrices
        val homogeneous = runnerPrices.by(event.side.opposite)
        val bestPrice = homogeneous.bestPrice
        return if (bestPrice != null) ValidationResult.OK else ValidationResult.DROP
    }

    override fun getProposedPrice(event: BetEvent): Double {
        val side = event.side
        val bestPrice = event.runnerPrices.by(side.opposite).bestPrice?.price ?: error("no best price")
        check(step >= 0)
        return if (step priceEq 0.0) {
            bestPrice
        } else {
            Price.round(Pricing.downgrade(bestPrice, -step, side))
        }
    }
}
