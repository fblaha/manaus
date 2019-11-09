package cz.fb.manaus.reactor.betting.validator.common

import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.Validator

class AccountMoneyValidator(private val minimalAvailable: Double) : Validator {

    override fun validate(event: BetEvent): ValidationResult {
        val money = event.account.money
        return if (money != null)
            if (money.available > minimalAvailable) ValidationResult.OK else ValidationResult.NOP
        else
            ValidationResult.OK
    }
}
