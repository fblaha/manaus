package cz.fb.manaus.manila.validator

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.manila.ManilaBet
import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.ValidationResult.REJECT
import cz.fb.manaus.reactor.betting.validator.Validator
import org.springframework.stereotype.Component

@ManilaBet
@Component
object BestPriceRangeValidator : Validator {

    override fun validate(context: BetContext): ValidationResult {
        val bestBack = context.runnerPrices.getHomogeneous(Side.BACK).bestPrice
        return if (bestBack == null) REJECT else ValidationResult.of(bestBack.price in 1.2..2.5)
    }

}
