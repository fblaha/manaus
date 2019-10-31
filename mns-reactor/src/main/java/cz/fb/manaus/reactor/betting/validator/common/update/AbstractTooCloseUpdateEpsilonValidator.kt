package cz.fb.manaus.reactor.betting.validator.common.update

import cz.fb.manaus.core.provider.ProviderTag
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.validator.UpdateOnlyValidator
import cz.fb.manaus.reactor.betting.validator.ValidationResult

abstract class AbstractTooCloseUpdateEpsilonValidator(private val epsilon: Double) : UpdateOnlyValidator {

    override fun validate(event: BetEvent): ValidationResult {
        val oldOne = event.oldBet!!.requestedPrice.price
        val newOne = event.newPrice!!.price
        val epsilon = (oldOne - 1) * this.epsilon
        return if (newOne in oldOne - epsilon..oldOne + epsilon) ValidationResult.NOP else ValidationResult.OK
    }

    override val tags get() = setOf(ProviderTag.PriceShiftContinuous)
}

