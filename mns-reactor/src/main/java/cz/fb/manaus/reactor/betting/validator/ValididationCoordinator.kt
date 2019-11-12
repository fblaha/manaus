package cz.fb.manaus.reactor.betting.validator

import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.proposer.PriceProposer

class ValidationCoordinator(
        validators: List<Validator>,
        private val validationService: ValidationService) {

    private val validators = validators.partition { it is PriceProposer }

    fun validatePrePrice(betEvent: BetEvent): ValidationResult {
        val (prePriceValidators, _) = validators
        return validate(betEvent, prePriceValidators)
    }

    fun validatePrice(betEvent: BetEvent): ValidationResult {
        val (_, priceValidators) = validators
        return validate(betEvent, priceValidators)
    }

    private fun validate(betEvent: BetEvent, prePriceValidators: List<Validator>): ValidationResult {
        return validationService.validate(betEvent, prePriceValidators)
    }


}

