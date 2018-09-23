package cz.fb.manaus.reactor.betting.validator.common

import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.Validator

open class AbstractAccountMoneyValidator(private val minimalAvailable: Double) : Validator {

    override fun validate(context: BetContext): ValidationResult {
        val money = context.accountMoney
        return if (money != null)
            ValidationResult.of(money.available > minimalAvailable)
        else
            ValidationResult.ACCEPT
    }
}
