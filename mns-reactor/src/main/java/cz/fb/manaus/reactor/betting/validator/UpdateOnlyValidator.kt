package cz.fb.manaus.reactor.betting.validator

interface UpdateOnlyValidator : Validator {

    override val isUpdateOnly: Boolean
        get() = true
}