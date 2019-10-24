package cz.fb.manaus.reactor.betting.validator.common

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.model.priceEq
import cz.fb.manaus.core.provider.ProviderCapability
import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.Validator

abstract class AbstractLastMatchedValidator(private val passEqual: Boolean) : Validator {

    override val capabilities: Set<ProviderCapability>
        get() = setOf(ProviderCapability.LastMatchedPrice)

    override fun validate(context: BetContext): ValidationResult {
        val lastMatchedPrice = context.runnerPrices.lastMatchedPrice ?: return ValidationResult.REJECT
        if (context.newPrice!!.price priceEq lastMatchedPrice) {
            return ValidationResult.of(passEqual)
        }
        val side = context.side
        return if (side === Side.LAY) {
            ValidationResult.of(context.newPrice!!.price < lastMatchedPrice)
        } else {
            ValidationResult.of(context.newPrice!!.price > lastMatchedPrice)
        }
    }

}
