package cz.fb.manaus.reactor.betting.validator.common.update

import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.validator.UpdateOnlyValidator
import cz.fb.manaus.reactor.betting.validator.ValidationResult

class TooCloseUpdateEpsilonValidator(private val epsilon: Double) : UpdateOnlyValidator {

    override fun validate(event: BetEvent): ValidationResult {
        val oldOne = event.oldBet?.remote?.requestedPrice?.price ?: error("no oldBet")
        val newOne = event.proposedPrice!!.price
        val epsilon = (oldOne - 1) * this.epsilon
        return if (newOne in oldOne - epsilon..oldOne + epsilon) ValidationResult.NOP else ValidationResult.OK
    }

}

