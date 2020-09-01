package cz.fb.manaus.reactor.betting.validator

import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.proposer.PriceProposer

class ValidationCoordinator(
    validators: List<Validator>,
    private val validationService: ValidationService
) {

    private val validators = validators.partition { it is PriceProposer }

    fun validatePrePrice(betEvent: BetEvent): ValidationResult {
        val (prePriceValidators, _) = validators
        val validate = validationService.validator(prePriceValidators)
        return validate(betEvent)
    }

    fun validatePrice(betEvent: BetEvent): ValidationResult {
        val (_, priceValidators) = validators
        val validate = validationService.validator(priceValidators)
        return validate(betEvent)
    }

}

