package cz.fb.manaus.reactor.betting.validator.common

import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.Validator
import java.util.Objects.requireNonNull

abstract class AbstractLastMatchedValidator(private val passEqual: Boolean) : Validator {

    override fun validate(context: BetContext): ValidationResult {
        val lastMatchedPrice = context.runnerPrices.lastMatchedPrice ?: return ValidationResult.REJECT
        if (Price.priceEq(context.newPrice!!.price, lastMatchedPrice)) {
            return ValidationResult.of(passEqual)
        }
        val side = requireNonNull(context.side)
        return if (side === Side.LAY) {
            ValidationResult.of(context.newPrice!!.price < lastMatchedPrice)
        } else {
            ValidationResult.of(context.newPrice!!.price > lastMatchedPrice)
        }
    }

}
