package cz.fb.manaus.reactor.betting.proposer

import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.betting.NameAware
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.Validator
import java.util.*

interface PriceProposer : Validator, NameAware {

    val isMandatory: Boolean
        get() = true

    fun getProposedPrice(context: BetContext): OptionalDouble


    override val isDowngradeAccepting: Boolean
        get() = false


    override val isPriceRequired: Boolean
        get() = false


    override fun validate(context: BetContext): ValidationResult {
        return ValidationResult.ACCEPT
    }
}
