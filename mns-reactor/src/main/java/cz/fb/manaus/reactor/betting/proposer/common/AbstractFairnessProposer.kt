package cz.fb.manaus.reactor.betting.proposer.common

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.price.PriceService
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractFairnessProposer(private val side: Side, private val downgradeStrategy: (BetContext) -> Double) : PriceProposer {
    @Autowired
    private lateinit var priceService: PriceService

    override fun validate(context: BetContext): ValidationResult {
        return ValidationResult.of(context.metrics.fairness[side] != null)
    }

    override fun getProposedPrice(context: BetContext): Double {
        val fairness = context.metrics.fairness[side]!!
        val bestPrice = context.runnerPrices.getHomogeneous(side).bestPrice!!
        val fairPrice = priceService.getFairnessFairPrice(bestPrice.price, fairness)
        val downgradeFraction = downgradeStrategy(context)
        return priceService.downgrade(fairPrice, downgradeFraction, context.side)
    }
}
