package cz.fb.manaus.reactor.betting.validator

import cz.fb.manaus.core.provider.ProviderSelector
import cz.fb.manaus.reactor.betting.BetEvent

interface Validator : ProviderSelector {

    val isDowngradeAccepting: Boolean
        get() = true

    val isUpdateOnly: Boolean
        get() = false

    fun validate(event: BetEvent): ValidationResult

}
