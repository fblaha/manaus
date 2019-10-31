package cz.fb.manaus.reactor.betting.proposer.common

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.rounding.RoundingService
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractBestPriceProposer(private val step: Int) : PriceProposer {

    @Autowired
    private lateinit var roundingService: RoundingService

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
        val provider = event.account.provider
        return if (step == 0) {
            bestPrice
        } else {
            if (side === Side.LAY) {
                roundingService.increment(bestPrice, step, provider::matches)
            } else {
                roundingService.decrement(bestPrice, step, provider.minPrice, provider::matches)
            }
        }
    }
}
