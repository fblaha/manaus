package cz.fb.manaus.reactor.betting.proposer.common

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.rounding.RoundingService
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractBestPriceProposer(private val step: Int) : PriceProposer {

    @Autowired
    private lateinit var roundingService: RoundingService

    override fun validate(context: BetContext): ValidationResult {
        val runnerPrices = context.runnerPrices
        val homogeneous = runnerPrices.getHomogeneous(context.side.opposite)
        val bestPrice = homogeneous.bestPrice
        return if (bestPrice != null) {
            ValidationResult.ACCEPT
        } else {
            ValidationResult.REJECT
        }
    }

    override fun getProposedPrice(context: BetContext): Double? {
        val side = context.side
        val bestPrice = context.runnerPrices.getHomogeneous(side.opposite).bestPrice!!.price
        check(step >= 0)
        val provider = context.account.provider
        return if (step == 0) {
            bestPrice
        } else {
            if (side === Side.LAY) {
                roundingService.increment(bestPrice, step, provider::hasCapabilities)
            } else {
                roundingService.decrement(bestPrice, step, provider.minPrice, provider::hasCapabilities)
            }
        }
    }
}
