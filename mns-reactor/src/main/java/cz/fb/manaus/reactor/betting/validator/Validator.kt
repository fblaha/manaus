package cz.fb.manaus.reactor.betting.validator

import cz.fb.manaus.core.provider.ProviderSelector
import cz.fb.manaus.reactor.betting.NameAware

interface Validator<T> : NameAware, ProviderSelector {

    val isDowngradeAccepting: Boolean
        get() = true

    val isUpdateOnly: Boolean
        get() = false

    val isPriceRequired: Boolean
        get() = true

    fun validate(event: T): ValidationResult

}
