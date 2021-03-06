package cz.fb.manaus.reactor.betting.proposer.common

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.price.Pricing

class FairnessProposer(
        private val side: Side,
        private val strategy: (BetEvent) -> Double?
) : PriceProposer {

    override fun validate(event: BetEvent): ValidationResult {
        return if (event.metrics.fairness[side] != null) ValidationResult.OK else ValidationResult.DROP
    }

    override fun getProposedPrice(event: BetEvent): Double {
        val fairness = event.metrics.fairness[side] ?: error("bad side")
        val bestPrice = event.runnerPrices.by(side).bestPrice ?: error("no best side")
        val fairPrice = Pricing.getFairnessFairPrice(bestPrice.price, fairness)
        val downgradeFraction = strategy(event) ?: error("no val provided")
        return Pricing.downgrade(fairPrice, downgradeFraction, event.side)
    }
}
