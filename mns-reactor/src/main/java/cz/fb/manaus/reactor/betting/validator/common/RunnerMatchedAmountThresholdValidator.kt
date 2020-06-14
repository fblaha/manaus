package cz.fb.manaus.reactor.betting.validator.common

import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.strategy.Strategy
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.Validator

class RunnerMatchedAmountThresholdValidator(private val thresholdStrategy: Strategy) : Validator {

    override fun validate(event: BetEvent): ValidationResult {
        val matchedAmount = event.runnerPrices.matchedAmount
        val threshold = thresholdStrategy(event) ?: error("no threshold")
        return when {
            matchedAmount != null && matchedAmount >= threshold -> ValidationResult.OK
            else -> ValidationResult.DROP
        }
    }
}
