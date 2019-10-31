package cz.fb.manaus.reactor.betting.validator.common

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.model.priceEq
import cz.fb.manaus.core.provider.ProviderTag.LastMatchedPrice
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.ValidationResult.ACCEPT
import cz.fb.manaus.reactor.betting.validator.ValidationResult.REJECT
import cz.fb.manaus.reactor.betting.validator.Validator

abstract class AbstractLastMatchedValidator(private val passEqual: Boolean) : Validator {

    override val tags get() = setOf(LastMatchedPrice)

    override fun validate(event: BetEvent): ValidationResult {
        val lastMatchedPrice = event.runnerPrices.lastMatchedPrice ?: return ValidationResult.REJECT
        if (event.newPrice!!.price priceEq lastMatchedPrice) {
            return if (passEqual) ACCEPT else REJECT
        }
        val side = event.side
        return if (side === Side.LAY) {
            if (event.newPrice!!.price < lastMatchedPrice) ACCEPT else REJECT
        } else {
            if (event.newPrice!!.price > lastMatchedPrice) ACCEPT else REJECT
        }
    }

}
