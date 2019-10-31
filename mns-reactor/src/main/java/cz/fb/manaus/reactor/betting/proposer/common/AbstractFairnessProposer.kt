package cz.fb.manaus.reactor.betting.proposer.common

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.proposer.DowngradeStrategy
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.price.PriceService
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractFairnessProposer(private val side: Side, private vararg val downgradeStrategies: DowngradeStrategy) : PriceProposer {
    @Autowired
    private lateinit var priceService: PriceService

    override fun validate(event: BetEvent): ValidationResult {
        return if (event.metrics.fairness[side] != null) ValidationResult.ACCEPT else ValidationResult.REJECT
    }

    override fun getProposedPrice(event: BetEvent): Double {
        val fairness = event.metrics.fairness[side]!!
        val bestPrice = event.runnerPrices.getHomogeneous(side).bestPrice!!
        val fairPrice = priceService.getFairnessFairPrice(bestPrice.price, fairness)
        val provider = event.account.provider
        val strategy = downgradeStrategies.find(provider::matches) ?: error("no such strategy")
        val downgradeFraction = strategy(event)
        return priceService.downgrade(fairPrice, downgradeFraction, event.side)
    }
}
