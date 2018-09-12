package cz.fb.manaus.reactor.betting.proposer.common

import com.google.common.base.Preconditions
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.rounding.RoundingService
import org.springframework.beans.factory.annotation.Autowired
import java.util.*
import java.util.Objects.requireNonNull

abstract class AbstractBestPriceProposer protected constructor(private val step: Int) : PriceProposer {

    @Autowired
    private lateinit var roundingService: RoundingService

    override fun validate(context: BetContext): ValidationResult {
        val runnerPrices = context.runnerPrices
        val homogeneous = runnerPrices.getHomogeneous(context.side.opposite)
        val bestPrice = homogeneous.bestPrice
        return if (bestPrice.isPresent) {
            ValidationResult.ACCEPT
        } else {
            ValidationResult.REJECT
        }
    }

    override fun getProposedPrice(context: BetContext): OptionalDouble {
        val side = requireNonNull(context.side)
        val bestPrice = context.runnerPrices.getHomogeneous(side.opposite).bestPrice.get().price
        Preconditions.checkState(step >= 0)
        return if (step == 0) {
            OptionalDouble.of(bestPrice)
        } else {
            if (side === Side.LAY) {
                roundingService.increment(bestPrice, step)
            } else {
                roundingService.decrement(bestPrice, step)
            }
        }
    }
}
