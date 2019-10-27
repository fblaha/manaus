package cz.fb.manaus.manila.validator

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.manila.ManilaBet
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.ValidationResult.REJECT
import cz.fb.manaus.reactor.betting.validator.Validator
import org.springframework.stereotype.Component

@ManilaBet
@Component
object BestPriceRangeValidator : Validator {

    override fun validate(event: BetEvent): ValidationResult {
        return when (val bestBack = event.runnerPrices.getHomogeneous(Side.BACK).bestPrice) {
            null -> REJECT
            else -> ValidationResult.of(bestBack.price in 1.2..2.5)
        }
    }

}
