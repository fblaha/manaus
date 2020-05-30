package cz.fb.manaus.reactor.betting.validator.common

import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.Validator

class RunnerMatchedAmountThresholdValidator(private val threshold: Double) : Validator {

    override fun validate(event: BetEvent): ValidationResult {
        val matchedAmount = event.runnerPrices.matchedAmount
        return when {
            matchedAmount != null && matchedAmount >= threshold -> ValidationResult.OK
            else -> ValidationResult.DROP
        }
    }
}
