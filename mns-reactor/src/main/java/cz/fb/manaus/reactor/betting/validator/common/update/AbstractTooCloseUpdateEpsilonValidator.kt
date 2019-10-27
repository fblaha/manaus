package cz.fb.manaus.reactor.betting.validator.common.update

import cz.fb.manaus.core.provider.ProviderTag
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.Validator

abstract class AbstractTooCloseUpdateEpsilonValidator(private val epsilon: Double) : Validator {

    override fun validate(event: BetEvent): ValidationResult {
        val oldOne = event.oldBet!!.requestedPrice.price
        val newOne = event.newPrice!!.price
        val epsilon = (oldOne - 1) * this.epsilon
        return ValidationResult.of(newOne !in oldOne - epsilon..oldOne + epsilon)
    }

    override val isUpdateOnly: Boolean = true

    override val tags get() = setOf(ProviderTag.PriceShiftContinuous)
}

