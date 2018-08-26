package cz.fb.manaus.manila.validator

import com.google.common.collect.Range
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.manila.ManilaBet
import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.ValidationResult.REJECT
import cz.fb.manaus.reactor.betting.validator.Validator
import org.springframework.stereotype.Component

@ManilaBet
@Component
class BestPriceRangeValidator : Validator {

    override fun validate(context: BetContext): ValidationResult {
        val bestBack = context.runnerPrices.getHomogeneous(Side.BACK).bestPrice
        return bestBack
                .map { price -> ValidationResult.of(RANGE.contains(price.price)) }
                .orElse(REJECT)
    }

    companion object {

        val RANGE = Range.closed(1.2, 2.5)
    }

}
