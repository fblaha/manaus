package cz.fb.manaus.reactor.betting.validator.common.update

import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.Validator

abstract class AbstractTooCloseUpdateEpsilonValidator(private val epsilon: Double) : Validator {

    override fun validate(context: BetContext): ValidationResult {
        val oldOne = context.oldBet!!.requestedPrice.price
        val newOne = context.newPrice!!.price
        val epsilon = (oldOne - 1) * this.epsilon
        return ValidationResult.of(newOne !in oldOne - epsilon..oldOne + epsilon)
    }

    override val isUpdateOnly: Boolean = true

}
