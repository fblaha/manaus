package cz.fb.manaus.reactor.betting.validator

import cz.fb.manaus.core.provider.ProviderCapability
import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.betting.NameAware

interface Validator : NameAware {

    val isDowngradeAccepting: Boolean
        get() = true

    val isUpdateOnly: Boolean
        get() = false

    val isPriceRequired: Boolean
        get() = true

    val requiredCapabilities: Set<ProviderCapability>
        get() = emptySet()

    fun validate(context: BetContext): ValidationResult

}
