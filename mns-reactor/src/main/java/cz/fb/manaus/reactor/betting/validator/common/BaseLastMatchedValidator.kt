package cz.fb.manaus.reactor.betting.validator.common

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.model.priceEq
import cz.fb.manaus.core.provider.ProviderTag.LastMatchedPrice
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.ValidationResult.DROP
import cz.fb.manaus.reactor.betting.validator.ValidationResult.OK
import cz.fb.manaus.reactor.betting.validator.Validator

class BaseLastMatchedValidator(private val passEqual: Boolean) : Validator {

    override val tags get() = setOf(LastMatchedPrice)

    override fun validate(event: BetEvent): ValidationResult {
        val lastMatchedPrice = event.runnerPrices.lastMatchedPrice ?: return DROP
        if (event.newPrice!!.price priceEq lastMatchedPrice) {
            return if (passEqual) OK else DROP
        }
        val side = event.side
        return if (side === Side.LAY) {
            if (event.newPrice!!.price < lastMatchedPrice) OK else DROP
        } else {
            if (event.newPrice!!.price > lastMatchedPrice) OK else DROP
        }
    }

}
