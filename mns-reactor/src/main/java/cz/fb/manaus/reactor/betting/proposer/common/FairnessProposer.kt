package cz.fb.manaus.reactor.betting.proposer.common

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.proposer.DowngradeStrategy
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.price.PriceService

class FairnessProposer(
        private val side: Side,
        private val priceService: PriceService,
        private val downgradeStrategy: DowngradeStrategy
) : PriceProposer {

    override fun validate(event: BetEvent): ValidationResult {
        return if (event.metrics.fairness[side] != null) ValidationResult.OK else ValidationResult.DROP
    }

    override fun getProposedPrice(event: BetEvent): Double {
        val fairness = event.metrics.fairness[side]!!
        val bestPrice = event.runnerPrices.getHomogeneous(side).bestPrice!!
        val fairPrice = priceService.getFairnessFairPrice(bestPrice.price, fairness)
        val downgradeFraction = downgradeStrategy(event) ?: error("no val provided")
        return priceService.downgrade(fairPrice, downgradeFraction, event.side)
    }
}
