package cz.fb.manaus.reactor.betting.proposer.common

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.price.PriceService
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

abstract class AbstractFairnessProposer(private val side: Side, private val downgradeStrategy: (BetContext) -> Double) : PriceProposer {
    @Autowired
    private lateinit var priceService: PriceService

    override fun validate(context: BetContext): ValidationResult {
        return ValidationResult.of(context.fairness.get(side).isPresent)
    }

    override fun getProposedPrice(context: BetContext): OptionalDouble {
        val fairness = context.fairness.get(side)
        val bestPrice = context.runnerPrices.getHomogeneous(side).bestPrice.get()
        val fairPrice = priceService.getFairnessFairPrice(bestPrice.price, fairness.asDouble)
        val downgradeFraction = downgradeStrategy(context)
        return OptionalDouble.of(priceService.downgrade(fairPrice, downgradeFraction, context.side))
    }
}
