package cz.fb.manaus.ischia.validator

import cz.fb.manaus.ischia.BackUniverse
import cz.fb.manaus.ischia.LayUniverse
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.Validator
import org.springframework.stereotype.Component

@BackUniverse
@LayUniverse
@Component
object AllRunnersMatchedValidator : Validator {

    // TODO use OPA
    override fun validate(event: BetEvent): ValidationResult {
        val prices = event.marketPrices
        if (prices.size > 2) return ValidationResult.OK
        val allMatched = prices.mapNotNull { it.matchedAmount }.all { it > 0.0 }
        return if (allMatched) ValidationResult.OK else ValidationResult.NOP
    }

}
